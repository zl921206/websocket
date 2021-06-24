package com.websocket.task;

import com.websocket.handler.WebSocketHandler;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 删除用户无用通道数据定时任务
 */
@Slf4j
@Component
@EnableScheduling
public class UserDisableChannelRemoveTask {

    /**
     * 每小时执行调用一次
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void execute(){
        log.info("开始执行删除无用的用户通道数据定时任务......");
        AtomicInteger removeCunt = new AtomicInteger(0);
        WebSocketHandler.sysTypeMap.values().forEach(map -> {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                Iterator<Channel> channelIterator = map.get(key).iterator();
                while (channelIterator.hasNext()) {
                    Channel channel = channelIterator.next();
                    if(!channel.isWritable()){
                        // 删除已经断开的用户通道，避免内存泄露，导致内存溢出
                        channelIterator.remove();
                        removeCunt.incrementAndGet();
                    }
                }
                if(map.get(key).size() == 0){
                    iterator.remove();
                }
            }
        });
        log.info("删除无用通道size：{}", removeCunt.get());
        log.info("结束执行删除无用的用户通道数据定时任务......");
    }
}
