package com.sil.club.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 密码(加密后)
     */
    private String password;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色: student/leader/admin
     */
    private String role;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
