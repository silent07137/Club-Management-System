package com.sil.club.utils;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;    // 状态码：200成功，500失败
    private String message;  // 提示信息
    private T data;          // 真正的数据内容

    // 快捷成功方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 快捷失败方法
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}