package com.websocket.service;

import com.websocket.dto.ResultDto;
import com.websocket.vo.MsgVO;
import io.netty.channel.Channel;
import java.util.List;

/**
 * 消息发送服务接口
 */
public interface MsgSendService {

    /**
     * 发送消息给指定用户
     * @param msgVo
     */
    public ResultDto sendMsgToUser(MsgVO msgVo);

    /**
     * 发送消息给指定系统指定类型的所有用户
     * @param msgVo
     * @return
     */
    ResultDto sendMsgToAll(MsgVO msgVo);

    /**
     * 消息发送
     * @param channel
     * @param msgVo
     * @return
     */
    public ResultDto sendMessage(Channel channel, MsgVO msgVo);

}
