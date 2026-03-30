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
        // 1. 在后端控制台打印详细的错误日志，方便我们自己排查 Bug
        log.error("系统发生未知异常: ", e);
        
        // 2. 给前端返回一个友好的提示（隐藏了具体的代码报错细节）
        return Result.error(500, "系统开小差了，请稍后再试！");
    }
    
    // 以后如果我们自定义了诸如 "UserNotFoundException" 等特定的异常，
    // 也可以继续在这里加方法来单独拦截处理！
}