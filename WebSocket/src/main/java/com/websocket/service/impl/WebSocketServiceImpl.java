package com.websocket.service.impl;

import com.websocket.handler.WebSocketHandler;
import com.websocket.service.WebSocketService;
import com.websocket.utils.StringUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * webSocket服务接口实现类
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Override
    public void disconnect(Integer sysType, Long userId, Integer userType){
        try {
            deleteUserChannel(WebSocketHandler.sysTypeMap.get(sysType).get(StringUtils.getUserIdentity(userId, userType)));
        } catch (Exception e){
            e.printStackTrace();
            log.error("删除无用的通道缓存对象异常：{}", e.getMessage());
        }
    }

    /**
     * 删除无用的通道缓存对象
     * @param channels
     */
    @Override
    public void deleteUserChannel(List<Channel> channels){
        if(!CollectionUtils.isEmpty(channels)){
            // 删除已经无用的通道
            Iterator<Channel> channelIterator = channels.iterator();
            while (channelIterator.hasNext()) {
                // 判断通道是否可写
                if (!channelIterator.next().isWritable()) {
                    channelIterator.remove();
                }
            }
        }
    }

    /**
     * 判断用户是否在线
     * @param channels true: 不在线，false：在线
     * @return
     */
    @Override
    public boolean userIsNotOnline(List<Channel> channels) {
        // 如果用户通道不存在，则返回 true
        if (CollectionUtils.isEmpty(channels)) {
            return true;
        }
        // 如果用户通道存在，则判断用户通道是否可用
        for (Channel channel : channels) {
            if (channel.isWritable()) {
                return false;
            }
        }
        deleteUserChannel(channels);
        return true;
    }
}
