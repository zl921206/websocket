package com.websocket.service.impl;

import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
import com.websocket.dto.ResultDto;
import com.websocket.handler.WebSocketHandler;
import com.websocket.service.MsgSendService;
import com.websocket.service.WebSocketService;
import com.websocket.vo.MsgVO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息发送服务实现类
 */
@Service
@Slf4j
public class MsgSendServiceImpl implements MsgSendService {

    /**
     * 发送消息给指定用户
     *
     * @param msgVo
     */
    @Override
    public ResultDto sendMsgToUser(MsgVO msgVo) {
        // 获取指定用户通道
        Channel channel = WebSocketHandler.userChannel.get(msgVo.getUserId());
        // 判断用户是否在线
        if (SpringContext.getBean(WebSocketService.class).userIsNotOnline(msgVo.getUserId(), channel)) {
            log.warn("用户ID：{}，不在线，消息发送失败！", msgVo.getUserId());
            return ResultDto.error(Constants.USER_NOT_ONLINE, "消息发送失败！需要推送的用户：" + msgVo.getUserId() + "，不在线！");
        }
        // 发送消息
        return sendMessage(channel, msgVo);

    }

    /**
     * 发送消息给所有用户
     *
     * @param msgVo
     * @return
     */
    @Override
    public ResultDto sendMsgToAll(MsgVO msgVo) {
        WebSocketHandler.channels.writeAndFlush(new TextWebSocketFrame(msgVo.getMsgContent()));
        return ResultDto.success("群发完成！");
    }

    /**
     * 消息发送
     * @param channel
     * @param msgVo
     * @return
     */
    @Override
    public ResultDto sendMessage(Channel channel, MsgVO msgVo) {
        String msg = msgVo.getMsgContent();
        try {
            channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    //写操作完成，并没有错误发生
                    if (future.isSuccess()) {
                        log.info("msg send successfully! msg content：{}", msg);
                    } else {
                        //记录错误
                        log.error("msg send failed，msg content：{}", msg);
                        future.cause().printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("message发送异常：{}， msg: {}", e.getMessage(), msg);
            return ResultDto.error("消息发送异常！");
        }
        return ResultDto.success("消息发送成功！");
    }
}
