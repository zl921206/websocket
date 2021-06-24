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
     * 系统类型
     */
    @NotNull(message = "系统类型不能为空")
    @Min(value = 1, message = "系统类型最小值为1")
    private Integer sysType;

    /**
     * 用户ID，建立连接时使用
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID最小值为1")
    private Long userId;
    /**
     * 用户类型，即：身份，建立连接时使用
     */
    @NotNull(message = "用户类型不能为空")
    @Min(value = 0, message = "用户类型最小值为0")
    private Integer userType;

    /**
     * 消息发送者用户ID
     */
    private Long fromUserId;
    /**
     * 消息发送者用户类型，即：身份
     */
    private Integer fromUserType;
    /**
     * 消息接收者用户ID
     */
    private Long toUserId;
    /**
     * 消息接收者用户类型，即：身份
     */
    private Integer toUserType;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String msgContent;

    /**
     * 消息内容类型（1-文字 2-图片 3-语言）
     */
    private Integer msgContentType;

    /**
     * 咨询订单ID
     */
    private String consultOrderId;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 会话类型
     */
    private Integer sessionType;
}
