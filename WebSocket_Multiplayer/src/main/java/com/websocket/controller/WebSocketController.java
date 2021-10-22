package com.websocket.controller;

import com.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * webSocket服务控制器
 */
@RestController
@RequestMapping("/webSocket")
@Slf4j
public class WebSocketController {

    @Resource
    private WebSocketService webSocketService;

    /**
     * 断开websocket连接，删除缓存数据
     * @param sysType
     * @param userId
     * @param userType
     */
    @GetMapping("/disconnect")
    public void disconnect(@RequestParam("sysType") Integer sysType, @RequestParam("userId") Long userId, @RequestParam("userType") String userType){
        log.info("断开websocket连接，请求参数：系统类型：{}，用户ID：{}，用户类型：{}", sysType, userId, userType);
        webSocketService.disconnect(sysType, userId, userType);
    }

}
