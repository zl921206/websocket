package com.websocket.enums;

/**
 * 用户类型枚举类
 */
public enum UserTypeEnum {

    VISITOR_TYPE(0, "游客"),
    MEMBER_TYPE(1, "会员"),
    DOCTOR_TYPE(2, "医生");

    private final int value;
    private final String name;

    UserTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
