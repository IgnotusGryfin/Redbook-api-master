package com.itcast.interceptor;

import com.itcast.thread.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 */
@Slf4j
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("前置拦截器");
        // 1. 获取用户id
        String userId = request.getHeader("userId");
        if (userId != null) {
            // 2. 设置用户id
            log.info("用户id为：{}", userId);
            UserThreadLocal.setUserId(Integer.valueOf(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        log.info("后置拦截器");
        UserThreadLocal.clear();
    }
}
