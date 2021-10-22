package com.websocket.enums;

/**
 * 会话类型枚举类
 */
public enum SessionTypeEnum {

    RECEIPT_NOTIFICATION(1, "回执通知"),
    CONVERSATION(2, "会话"),
    INSERT_ORDER (3, "订单创建通知"),
    HEARTBEAT_NOTIFICATION(4, "心跳通知"),
    CLOSED(5, "关闭"),
    ACCEPT_ORDER_NOTIFICATION(6, "通知用户已接单"),
    TRANSFER_ORDER(7, "转单通知"),
    NOT_ONLINE_NOTIFICATION(8, "接收者未在线通知"),
    SEND_VIDEO_NOTIFICATION(9, "发起视频通知"),
    QUEUE_NUM_NOTIFICATION(10, "排队人数通知"),
    SURPLUS_CHAT_INTERACTION_NUM_NOTIFICATION(11, "剩余聊天交互次数通知"),
    TRANSFER_ORDER_PRE_NOTIFICATION(12, "转单前置提醒医生"),
    TEXT_INSERT_ORDER(13, "图文订单创建通知"),
    TO_ALL_DOCTOR_INSERT(14, "通知所有医生有开方订单创建"),
    DOCTOR_RECOMEND_DRUG_URL_NOTIFICATION(15, "推药链接通知"),
    DRUG_ORDER_PAY_FINISHED(16, "推药已经支付完成"),
    ORDER_RECEIVING_LIST_REFRESH(17, "接单列表刷新"),
    EXCLUSIVE_DOCTOR_PRELIMINARY_SUMMARY(18, "专属医生问诊小结"),
    SYS_MSG_NOTIFY(19, "系统消息通知"),
    CONSULTATION_VIDEO_END(20, "问诊视频已结束"),
    TOKEN_EXPIRE_NOTIFY(21, "TOKEN失效通知");


    private final int code;
    private final String message;

    SessionTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
