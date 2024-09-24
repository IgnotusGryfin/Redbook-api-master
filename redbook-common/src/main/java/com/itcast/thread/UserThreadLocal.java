package com.itcast.thread;

/**
 * 用户线程变量
 */
public class UserThreadLocal {
    public static final ThreadLocal<Integer> userThreadLocal = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        userThreadLocal.set(userId);
    }

    public static Integer getUserId() {
        return userThreadLocal.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
