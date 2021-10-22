package com.websocket.init;

import com.websocket.constant.Constants;
import com.websocket.context.SpringContext;
import com.websocket.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChannelInit extends ChannelInitializer<SocketChannel> {

    @Value("${webSocket.protocol}")
    private String protocol;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.info("收到新的客户端连接: {}", socketChannel.toString());
        // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        socketChannel.pipeline().addLast(new HttpServerCodec());
        // 以块的方式来写的处理器(添加对于读写大数据流的支持)
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        // 对httpMessage进行聚合
        socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));

        // ================= 上述是用于支持http协议的 =============

        // 添加自己的handler
        socketChannel.pipeline().addLast(new WebSocketHandler());

        // websocket 服务器处理的协议，用于给指定的客户端进行连接访问的路由地址
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler(SpringContext.getPropertiesValue(Constants.WEBSOCKET_PROTOCOL_KEY), "WebSocket", true, 65536 * 10, true, true));

    }
}
