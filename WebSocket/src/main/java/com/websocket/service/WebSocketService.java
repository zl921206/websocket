package com.websocket.service;

import io.netty.channel.Channel;

import java.util.List;

/**
 * webSocket服务接口
 */
public interface WebSocketService {

    /**
     * 断开websocket连接，删除缓存数据
     * @param sysType
     * @param userId
     * @param userType
     */
    void disconnect(Integer sysType, Long userId, Integer userType);

    /**
     * 删除无用的通道缓存对象
     * @param channels
     */
    void deleteUserChannel(List<Channel> channels);

    /**
     * 判断用户是否在线
     * @param channels true: 不在线，false：在线
     * @return
     */
    boolean userIsNotOnline(List<Channel> channels);
}
