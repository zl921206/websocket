package com.websocket.utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map集合工具类
 */
public class MapUtils {

    /**
     * 获取map中匹配key键的集合数据并返回
     *
     * @param map       原Map集合
     * @param filters   匹配的字符串
     * @return
     */
    public static <T> Map<String, T> parseMapForFilter(Map<String, T> map, String filters) {
        if (map == null) {
            return null;
        } else {
            map = map.entrySet().stream()
                    .filter((e) -> e.getKey().contains(filters))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        }
        return map;
    }

}
