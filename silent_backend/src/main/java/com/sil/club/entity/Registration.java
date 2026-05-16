package com.sil.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 报名记录表
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Getter
@Setter
@TableName("registration")
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报名ID
     */
    @TableId(value = "reg_id", type = IdType.AUTO)
    private Long regId;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 状态: 1已报名, 2已取消, 3已签到
     */
    @TableField("status")
    private Integer status;

    /**
     * 签到时间
     */
    @TableField("sign_time")
    private LocalDateTime signTime;

    /**
     * 报名时间
     */
    @TableField("reg_time")
    private LocalDateTime regTime;


}
