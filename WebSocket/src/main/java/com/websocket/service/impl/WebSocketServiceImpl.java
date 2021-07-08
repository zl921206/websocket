package com.websocket.service.impl;

import com.websocket.handler.WebSocketHandler;
import com.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * webSocket服务接口实现类
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Override
    public void disconnect(String userId){
        try {
            deleteUserChannel(userId);
        } catch (Exception e){
            e.printStackTrace();
            log.error("删除无用的通道缓存对象异常：{}", e.getMessage());
        }
    }

    /**
     * 删除无用的通道缓存对象
     * @param userId
     */
    @Override
    public void deleteUserChannel(String userId){
        WebSocketHandler.userChannel.remove(userId);
    }

    /**
     * 判断用户是否在线
     * @param userId
     * @param channel  true: 不在线，false：在线
     * @return
     */
    @Override
    public boolean userIsNotOnline(String userId, Channel channel) {
        // 判断用户通道是否可用
        if (channel.isWritable()) {
            return false;
        }
        deleteUserChannel(userId);
        return true;
    }
}
