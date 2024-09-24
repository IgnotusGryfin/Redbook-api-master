package com.itcast.util;

/**
 * 小红书号工具类
 */
public class NumberUtil {
    public static Long getNumber() {
        // 获取当前时间的时间戳（毫秒级）
        long timestampMillis = System.currentTimeMillis();

        // 将毫秒级时间戳转换为秒级（10位数字）
        return timestampMillis / 1000;
    }
}
