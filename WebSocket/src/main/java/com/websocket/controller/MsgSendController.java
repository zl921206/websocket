package com.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.websocket.config.RequestParameterVerify;
import com.websocket.dto.ResultDto;
import com.websocket.service.MsgSendService;
import com.websocket.vo.MsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 消息发送控制器
 */
@RestController
@RequestMapping("/msg")
@Slf4j
public class MsgSendController {

    @Resource
    private MsgSendService msgSendService;

    /**
     * 发送消息给指定用户
     * @param msgVo
     */
    @PostMapping("/sendMsgToUser")
    public ResultDto sendMsgToUser(@RequestBody MsgVO msgVo){
        log.info("给指定用户发送消息请求参数：{}", JSONObject.toJSONString(msgVo));
        // 参数校验
        RequestParameterVerify.validate(msgVo);
        return msgSendService.sendMsgToUser(msgVo);
    }

    /**
     * 发送消息给指定系统指定类型的所有用户
     * @param msgVo
     */
    @PostMapping("/sendMsgToAll")
    public ResultDto sendMsgToAll(@RequestBody MsgVO msgVo){
        log.info("发送消息给指定系统指定类型的所有用户，接口请求参数：{}", JSONObject.toJSONString(msgVo));
        return msgSendService.sendMsgToAll(msgVo);
    }

}
