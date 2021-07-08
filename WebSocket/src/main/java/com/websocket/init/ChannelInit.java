package com.websocket.init;

import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
import com.websocket.handler.HearBeatHandler;
import com.websocket.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通道初始化器
 * 用来加载通道处理器（ChannelHandler)
 */
@Slf4j
@Component
public class ChannelInit extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化通道
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.info("收到新的客户端连接: {}", socketChannel.toString());
        // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        socketChannel.pipeline().addLast(new HttpServerCodec());
        // 以块的方式来写的处理器(添加对于读写大数据流的支持)
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        // 对httpMessage进行聚合，主要是将HttpMessage聚合成FullHttpRequest/Response
        socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
        // ================= 上述是用于支持http协议的 =============

        // 添加自定义的handler
        socketChannel.pipeline().addLast(new WebSocketHandler());

        // websocket服务器处理的协议，用于给指定的客户端进行连接访问的路由地址,必须使用以ws、wss后缀结尾的url才能访问
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler(SpringContext.getPropertiesValue(Constants.WEBSOCKET_PROTOCOL_KEY), "WebSocket", true, 65536 * 10, true, true));

        // 增加心跳事件支持
        // 第一个参数:  读空闲4秒
        // 第二个参数： 写空闲8秒
        // 第三个参数： 读写空闲12秒
//        socketChannel.pipeline().addLast(new IdleStateHandler(4,8,12));
//        socketChannel.pipeline().addLast(new HearBeatHandler());
    }
}
