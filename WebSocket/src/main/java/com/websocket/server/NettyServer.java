package com.websocket.server;

import com.websocket.init.ChannelInit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty Reactor模型 -
 * 1.单线程模型:一个用户一个线程来处理，线程有极限
 * 2.多线程模型：加入线程池,线程池线程轮询执行任务
 * 3.主从多线程模型：俩个线程池，一个线程池接收请求，一个线程池处理IO（推荐，适用高并发环境）
 *
 * 以下代码为主从多线程模型
 * */
@Slf4j
public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        /**
         * 主线程池
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        /**
         * 从线程池
         */
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * 1：创建netty服务器启动对象
             */
            ServerBootstrap sb = new ServerBootstrap();
            // 配置TCP参数，握手字符串长度设置
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            // TCP_NODELAY算法，尽可能发送大块数据，减少充斥的小块数据
            sb.option(ChannelOption.TCP_NODELAY, true);
            // 开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
            sb.childOption(ChannelOption.SO_KEEPALIVE, true);
            /**
             * 2：初始化
             */
            // 绑定线程池
            sb.group(bossGroup, workerGroup)
                    // 指定使用的channel类型
                    .channel(NioServerSocketChannel.class)
                    // 绑定监听端口
                    .localAddress(this.port)
                    // 指定通道初始化器用来加载当Channel收到事件消息后，如何进行业务处理
                    .childHandler(new ChannelInit());
            /**
             * 3：绑定端口，以同步的方式启动
             */
            ChannelFuture cf = sb.bind().sync();
            // 日志打印
            log.info(NettyServer.class + " 启动正在监听： " + cf.channel().localAddress());
            /**
             * 4.等待服务关闭
             */
            cf.channel().closeFuture().sync();
        } finally {
            /**
             *  5：释放线程池资源
             */
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }
}