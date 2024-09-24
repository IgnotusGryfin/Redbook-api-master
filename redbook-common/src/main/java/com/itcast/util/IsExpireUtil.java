package com.itcast.util;

import java.util.Date;

/**
 * 判断是否过期
 */
public class IsExpireUtil {

    public static boolean isExpire(Date expireTime) {
        return expireTime.before(new Date());
    }
}
