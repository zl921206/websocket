package com.websocket.channel;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户通道map集合
 * @author Administrator
 * 用户通道集合（用户可能存在多个端登录，故而一个用户对应多个通道） 数据格式：Map<用户ID，用户通道集合>
 */
public class UserChannelMap extends ConcurrentHashMap<String, List<Channel>> {

    public static UserChannelMap USER_CHANNEL_MAP = null;

    /**
     * 添加用户通道
     * @param userChannelMap
     * @param key
     * @param channel
     */
    public static void putUserChannel(UserChannelMap userChannelMap, String key, Channel channel){
        if (!userChannelMap.containsKey(key)) {
            userChannelMap.put(key, new ArrayList<>(Arrays.asList(channel)));
        } else {
            if (!userChannelMap.get(key).contains(channel)) {
                userChannelMap.get(key).add(channel);
            }
        }
    }

    /**
     * 创建用户通道，只要调用该方法，就创建一个新的用户通道
     * @param userIdentity
     * @param channel
     * @return
     */
    public static UserChannelMap createUserChannel(String userIdentity, Channel channel){
        USER_CHANNEL_MAP = new UserChannelMap();
        USER_CHANNEL_MAP.put(userIdentity, new ArrayList<>(Arrays.asList(channel)));
        return USER_CHANNEL_MAP;
    }

}
