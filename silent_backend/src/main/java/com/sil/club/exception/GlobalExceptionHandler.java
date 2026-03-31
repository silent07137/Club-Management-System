package com.sil.club.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sil.club.vo.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice // 这个注解告诉 Spring Boot：请用我来接管所有的异常！
public class GlobalExceptionHandler {

    /**
     * 捕获所有 Exception 类型的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统发生未知异常: ", e);
        return Result.error(500, "系统开小差了，请稍后再试！");
    }
}