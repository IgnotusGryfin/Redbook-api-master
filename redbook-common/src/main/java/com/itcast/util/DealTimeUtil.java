package com.itcast.util;

/**
 * 处理时间工具类
 */
public class DealTimeUtil {

    public static String dealTime(Integer days) {
        String dealTime = "";
        if (days <= 3 && days > 0) {
            dealTime = days + "天前";
        } else if (days == 0) {
            dealTime = "今天";
        }
        return dealTime;
    }
}
