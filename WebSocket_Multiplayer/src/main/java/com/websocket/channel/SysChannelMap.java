package com.websocket.channel;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统通道map集合
 * @author Administrator
 * 定义系统对应的在线用户集合    数据格式：Map<系统类型，Map<用户ID，用户通道集合>>
 */
public class SysChannelMap extends ConcurrentHashMap<Integer, UserChannelMap> {

    public static SysChannelMap SYS_CHANNEL_MAP = new SysChannelMap();

    /**
     * 添加系统通道map
     * @param sysType
     * @param userIdentity
     * @param channel
     */
    public static void putSysChannel(Integer sysType, String userIdentity, Channel channel){
        if(SYS_CHANNEL_MAP.get(sysType) == null){
            SYS_CHANNEL_MAP.put(sysType, UserChannelMap.createUserChannel(userIdentity, channel));
            return;
        }
        UserChannelMap.putUserChannel(SYS_CHANNEL_MAP.get(sysType), userIdentity, channel);
    }

    /**
     * 获取用户通道map集合
     * @param key
     * @return
     */
    public static UserChannelMap getValue(Integer key){
        return SYS_CHANNEL_MAP.get(key);
    }

}
