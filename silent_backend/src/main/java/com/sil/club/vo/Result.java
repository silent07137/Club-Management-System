package com.sil.club.vo;

import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> {
    
    private Integer code; // 状态码
    private String message; // 提示信息
    private T data; // 返回的数据

    // 私有化构造方法，强制使用提供的静态方法
    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 成功响应 ==========

    /**
     * 成功，不带数据
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功，携带返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功，自定义提示信息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ========== 失败响应 ==========

    /**
     * 失败，返回通用错误码 500 和错误信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败，自定义错误码和错误信息 (比如 401未登录, 403无权限)
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}