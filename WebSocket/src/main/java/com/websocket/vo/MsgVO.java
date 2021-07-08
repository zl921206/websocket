package com.websocket.vo;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 消息对象
 */
@Data
public class MsgVO implements Serializable {

    /**
     * 用户ID，建立连接时使用
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID最小值为1")
    private String userId;

    /**
     * 消息发送者用户ID
     */
    private String fromUserId;
    /**
     * 消息接收者用户ID
     */
    private String toUserId;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String msgContent;


}
