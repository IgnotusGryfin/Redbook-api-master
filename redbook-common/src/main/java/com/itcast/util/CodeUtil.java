package com.itcast.util;

import java.util.Random;

/**
 * 验证码工具类
 */
public class CodeUtil {
    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(new Random().nextInt(10));
        }
        return sb.toString();
    }
}
