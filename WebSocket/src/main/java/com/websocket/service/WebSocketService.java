package com.websocket.service;

import io.netty.channel.Channel;

import java.util.List;

/**
 * webSocket服务接口
 */
public interface WebSocketService {

    /**
     * 断开websocket连接，删除缓存数据
     * @param userId
     */
    void disconnect(String userId);

    /**
     * 删除无用的通道缓存对象
     * @param userId
     */
    void deleteUserChannel(String userId);

    /**
     * 判断用户是否在线
     * @param userId
     * @param channel
     * @return
     */
    boolean userIsNotOnline(String userId, Channel channel);
}
