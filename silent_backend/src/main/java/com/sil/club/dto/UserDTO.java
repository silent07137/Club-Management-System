package com.sil.club.dto;

import lombok.Data;

/**
 * 接收前端登录/注册传来的参数
 */
@Data
public class UserDTO {
    private String studentId; // 学号
    private String password;  // 密码
    private String name;      // 姓名（注册时需要）
}