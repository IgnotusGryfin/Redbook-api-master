package com.itcast.handle;

import com.itcast.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(Exception e) {
        log.error("异常信息：{}", e.getMessage());
        return Result.failure(e.getMessage());
    }
}
