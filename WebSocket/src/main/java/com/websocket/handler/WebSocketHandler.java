package com.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.websocket.config.RequestParameterVerify;
import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanglei
 * @desc 自定义服务器端处理handler，继承SimpleChannelInboundHandler，处理WebSocket 连接数据
 * @date 2021-05-18
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 定义所有用户通道集合
     */
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户通道集合 数据格式：Map<用户ID，用户通道>
     */
    public static ConcurrentHashMap<String, Channel> userChannel = new ConcurrentHashMap<>(1000);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception { // (1)
        log.info("netty客户端收到服务器数据, 客户端地址: {}, msg: {}", ctx.channel().remoteAddress(), msg.text());
        // 消息转换
        MsgVO msgVO = JSONObject.parseObject(msg.text(), MsgVO.class);
        if (StringUtils.isEmpty(msgVO.getMsgContent())) {
            log.warn("消息内容不能为空！");
            return;
        }
        // 获取消息接收者通道
        Channel channel = userChannel.get(msgVO.getToUserId());
        // 判断消息接收者是否在线
        if (SpringContext.getBean(WebSocketService.class).userIsNotOnline(msgVO.getToUserId(), channel)) {
            log.info("消息接收者：{}，未在线！", msgVO.getUserId());
            return;
        }
        // 消息发送
        SpringContext.getBean(MsgSendService.class).sendMessage(channel, msgVO);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        }
        channels.add(ctx.channel());
        System.out.println("Client:" + incoming.remoteAddress() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        }
        System.out.println("Client:" + incoming.remoteAddress() + "离开");
        channels.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
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
            // 参数校验
            RequestParameterVerify.validate(msgVO);
            //如果url包含参数，需要处理
            if (uri.contains(Constants.QUESTION_MARK)) {
                // 用户连接，存入通道集合
                userChannel.put(msgVO.getUserId(), ctx.channel());
            }
            // 重新设置请求地址为WebSocketServerProtocolHandler 匹配的地址(如果WebSocketServerProtocolHandler 的时候checkStartsWith   为true则不需要设置，会根据前缀匹配)
            request.setUri(SpringContext.getPropertiesValue(Constants.WEBSOCKET_PROTOCOL_KEY));
        }
        // 接着建立请求
        super.channelRead(ctx, msg);
    }

}
