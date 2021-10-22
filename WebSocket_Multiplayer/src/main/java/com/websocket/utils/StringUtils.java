package com.websocket.utils;

import com.websocket.constant.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断对象是否为空
     *
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) {
        boolean result = false;
        if (o == null) {
            result = true;
        } else {
            if ("".equals(o.toString())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取get请求url参数
     *
     * @param url
     * @return
     */
    public static Map getUrlParams(String url) {
        Map<String, String> map = new HashMap<>();
        url = url.replace("?", ";");
        if (!url.contains(";")) {
            return map;
        }
        if (url.split(";").length > 0) {
            String[] arr = url.split(";")[1].split("&");
            for (String s : arr) {
                String key = s.split("=")[0];
                String value = s.split("=")[1];
                map.put(key, value);
            }
            return map;

        } else {
            return map;
        }
    }

    /**
     * 将用户ID与用户类型拼接，作为用户唯一身份（防止不同的类型用户存在ID一致的情况）
     * @param userId
     * @param userType
     * @return
     */
    public static String getUserIdentity(Long userId, Integer userType){
        return userId + Constants.JOINT_MARK + userType;
    }

    /**
     * 将用户ID与用户类型拼接，作为用户唯一身份（防止不同的类型用户存在ID一致的情况）
     * @param userId
     * @param userType
     * @return
     */
    public static String getUserIdentity(Long userId, String userType){
        return userId + Constants.JOINT_MARK + userType;
    }

}
