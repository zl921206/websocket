package com.websocket.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
import com.websocket.dto.ResultDto;
import com.websocket.enums.SysTypeEnum;
import com.websocket.handler.WebSocketHandler;
import com.websocket.service.MsgSendService;
import com.websocket.service.RetryService;
import com.websocket.service.WebSocketService;
import com.websocket.utils.MapUtils;
import com.websocket.utils.StringUtils;
import com.websocket.vo.MsgVO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息发送服务实现类
 */
@Service
@Slf4j
public class MsgSendServiceImpl implements MsgSendService {

    @Value("${sysMsgRecord.yaokangfu.url}")
    private String yaoKangFuSysUrl;
    @Value("${sysMsgRecord.onlineDoctor.url}")
    private String onlineDoctorSysUrl;
    @Value("${sysMsgRecord.onlinePrescription.url}")
    private String onlinePrescriptionSysUrl;

    @Resource
    RetryService retryService;

    /**
     * 发送消息给指定用户
     *
     * @param msgVo
     */
    @Override
    public ResultDto sendMsgToUser(MsgVO msgVo) {
        // 判断对应系统是否有用户在线
        ConcurrentHashMap<String, List<Channel>> sysMap = WebSocketHandler.sysTypeMap.get(msgVo.getSysType());
        if (CollectionUtils.isEmpty(sysMap)) {
            return ResultDto.error(Constants.USER_NOT_ONLINE, "消息发送失败！系统：" + SysTypeEnum.valueOf(msgVo.getSysType()).getName() + "，没有用户在线！");
        }
        // 获取接收消息的用户标识
        String userIdentity = StringUtils.getUserIdentity(msgVo.getUserId(), msgVo.getUserType());
        // 获取系统内的在线用户
        List<Channel> userChannelList = sysMap.get(userIdentity);
        // 判断用户是否在线
        if (SpringContext.getBean(WebSocketService.class).userIsNotOnline(userChannelList)) {
            return ResultDto.error(Constants.USER_NOT_ONLINE, "消息发送失败！系统：" + SysTypeEnum.valueOf(msgVo.getSysType()).getName() + "，需要推送的用户：" + userIdentity + "，不在线！");
        }
        // 发送消息
        return sendMessage(userChannelList, msgVo, false);

    }

    /**
     * 发送消息给指定系统指定类型的所有用户
     * @param msgVo
     * @return
     */
    @Override
    public ResultDto sendMsgToAll(MsgVO msgVo) {
        // 取出传入系统的所有用户通道
        Map<String, List<Channel>> allUserMap = WebSocketHandler.sysTypeMap.get(msgVo.getSysType());
        // 判断传入系统所有用户集合
        if(!CollectionUtils.isEmpty(allUserMap)){
            // 根据传入用户类型过滤，重新组装集合
            allUserMap = MapUtils.parseMapForFilter(allUserMap, Constants.JOINT_MARK + msgVo.getUserType());
            // 判断需要推送用户类型的集合数据是否为空
            if(!CollectionUtils.isEmpty(allUserMap)){
                allUserMap.values().forEach(userChannel -> {
                    // 发送消息
                    sendMessage(userChannel, msgVo, false);
                });
            }
        }
        return null;
    }

    /**
     * 消息发送
     *
     * @param channelList
     * @param msgVo
     * @param isNeedPush
     * @return
     */
    @Override
    public ResultDto sendMessage(List<Channel> channelList, MsgVO msgVo, boolean isNeedPush) {
        AtomicInteger initValue = new AtomicInteger(0);
        String msg = JSONObject.toJSONString(msgVo);
        try {
            // 推送消息给多端用户，并删除已经无用的通道
            Iterator<Channel> channelIterator = channelList.iterator();
            while (channelIterator.hasNext()) {
                Channel channel = channelIterator.next();
                // 判断通道是否可写
                if (channel.isWritable()) {
                    channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            //写操作完成，并没有错误发生
                            if (future.isSuccess()) {
                                // 保证同一会话只会调用一次
                                if (isNeedPush && initValue.incrementAndGet() == 1) {
                                    msgDataPushSys(msg, msgVo.getSysType());
                                }
                                log.info("msg send successfully! msg content：{}", msg);
                            } else {
                                //记录错误
                                log.error("msg send failed，msg content：{}", msg);
                                future.cause().printStackTrace();
                            }
                        }
                    });
                } else {
                    // 删除已经断开的用户通道，避免内存泄露，导致内存溢出
                    channelIterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("message发送异常：{}， msg: {}", e.getMessage(), msg);
            return ResultDto.error("消息发送异常！");
        }
        return ResultDto.success("消息发送成功！");
    }

    /**
     * 消息数据推送对应系统
     *
     * @param msgData
     * @param sysType
     * @return
     */
    @Override
    public void msgDataPushSys(String msgData, Integer sysType) {
        String sendUrl = "";
        SysTypeEnum sysTypeEnum = SysTypeEnum.valueOf(sysType);
        // 根据系统类型执行获取相应的系统url
        switch (sysTypeEnum) {
            case MEDICINE_RECOVERY:
                sendUrl = yaoKangFuSysUrl;
                break;
            case ONLINE_DOCTOR:
                sendUrl = onlineDoctorSysUrl;
                break;
            case ONLINE_PRESCRIPTION:
                sendUrl = onlinePrescriptionSysUrl;
                break;
            default:
        }
        if (StringUtils.isEmpty(sendUrl)) {
            log.error("系统类型错误！");
            return;
        }
        try {
            retryService.retryHttpReq(sendUrl, msgData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
