package com.websocket.enums;

/**
 * 系统类型枚举类
 */
public enum SysTypeEnum {

    MEDICINE_RECOVERY(1, "药康夫系统"),
    ONLINE_DOCTOR(2, "在线医生系统"),
    ONLINE_PRESCRIPTION(3, "在线处方平台系统");

    private final int value;
    private final String name;

    SysTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SysTypeEnum valueOf(Integer value) {
        for (SysTypeEnum sysTypeEnum : SysTypeEnum.values()) {
            if (value.equals(sysTypeEnum.value)) {
                return sysTypeEnum;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
