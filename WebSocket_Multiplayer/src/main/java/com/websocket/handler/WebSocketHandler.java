package com.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.websocket.channel.SysChannelMap;
import com.websocket.config.RequestParameterVerify;
import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
import com.websocket.enums.SessionTypeEnum;
import com.websocket.enums.SysTypeEnum;
import com.websocket.service.MsgSendService;
import com.websocket.service.WebSocketService;
import com.websocket.utils.StringUtils;
import com.websocket.vo.MsgVO;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanglei
 * @desc 自定义服务器端处理handler，继承SimpleChannelInboundHandler，处理WebSocket 连接数据
 * @date 2021-05-18
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 通道组，存放所有系统所有用户
     */
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 每当服务端收到新的客户端连接时,客户端的channel存入ChannelGroup列表中,并通知列表中其他客户端channel
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * 每当服务端断开客户端连接时,客户端的channel从ChannelGroup中移除,并通知列表中其他客户端channel
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }


    /**
     * 每当从服务端读到客户端写入信息时,将信息转发给其他客户端的Channel.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("netty客户端收到服务器数据, 客户端地址: {}, msg: {}", ctx.channel().remoteAddress(), msg.text());
        // 消息转换
        MsgVO msgVO = JSONObject.parseObject(msg.text(), MsgVO.class);
        if (StringUtils.isEmpty(msgVO.getMsgContent())) {
            log.warn("消息内容不能为空！");
            return;
        }
        // 获取消息接收者用户身份标识
        String userIdentity = StringUtils.getUserIdentity(msgVO.getToUserId(), msgVO.getToUserType());
        // 判断消息接收者通道是否已经建立
        if (SpringContext.getBean(WebSocketService.class).userIsNotOnline(SysChannelMap.getValue(msgVO.getSysType()).get(userIdentity))) {
            log.info("消息接收者：{}，未在线！", userIdentity);
            // 接收者用户未在线，直接将聊天记录写入业务系统
            SpringContext.getBean(MsgSendService.class).msgDataPushSys(JSONObject.toJSONString(msgVO), msgVO.getSysType());
            return;
        }
        // 消息处理类
        SpringContext.getBean(MsgSendService.class).sendMessage(SysChannelMap.getValue(msgVO.getSysType()).get(userIdentity), msgVO, true);
    }

    /**
     * 当服务端的IO 抛出异常时被调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        log.error("SimpleChatClient:" + incoming.remoteAddress() + "异常", cause);
        //异常出现就关闭连接
        ctx.close();
    }

    /**
     * 当用户建立连接时，将用户信息存入通道
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("执行channelRead().....请求参数：{}", msg);
        if (null != msg && msg instanceof FullHttpRequest) {
            // 转化为http请求
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();

            Map paramMap = StringUtils.getUrlParams(uri);
            log.info("用户上线，请求参数：" + JSON.toJSONString(paramMap));
            MsgVO msgVO = JSONObject.parseObject(JSON.toJSONString(paramMap), MsgVO.class);
            //如果url包含参数，需要处理
            if (uri.contains(Constants.QUESTION_MARK)) {
                // 参数校验
                RequestParameterVerify.validate(msgVO);
                // 获取用户身份标识
                String userIdentity = StringUtils.getUserIdentity(msgVO.getUserId(), msgVO.getUserType());
                // 用户连接，存入通道集合
                SysChannelMap.putSysChannel(msgVO.getSysType(), userIdentity, ctx.channel());
                // 删除当前用户已经关闭的缓存通道
                SpringContext.getBean(WebSocketService.class).deleteUserChannel(SysChannelMap.getValue(msgVO.getSysType()).get(userIdentity));
                log.info("当前系统：{}，在线人数：{}，当前用户：{}，用户所属终端数：{}", SysTypeEnum.valueOf(msgVO.getSysType()).getName(), SysChannelMap.getValue(msgVO.getSysType()).size(), userIdentity, SysChannelMap.getValue(msgVO.getSysType()).get(userIdentity).size());
            }
            // 重新设置请求地址为WebSocketServerProtocolHandler 匹配的地址(如果WebSocketServerProtocolHandler 的时候checkStartsWith   为true则不需要设置，会根据前缀匹配)
            request.setUri(SpringContext.getPropertiesValue(Constants.WEBSOCKET_PROTOCOL_KEY));
        }
        // 接着建立请求
        super.channelRead(ctx, msg);
    }

}
