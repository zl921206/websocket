package com.websocket;

import com.websocket.server.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import javax.annotation.PostConstruct;

/**
 * webSocket服务启动类
 *
 * @author zhanglei
 */
@SpringBootApplication
@Slf4j
@EnableRetry // 开启重试机制
public class WebSocketServer {

    // 定义webSocket端口号
    private static int webSocketPort;
    @Value("${webSocket.port}")
    private Integer port;

    @PostConstruct
    public void init(){
        webSocketPort = port;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketServer.class, args);
        // 启动netty服务器
        try {
            new NettyServer(webSocketPort).start();
        } catch (Exception e) {
            System.out.println("NettyServerError:" + e.getMessage());
        }
        log.info("SpringBootApp start success ......");
    }
}
