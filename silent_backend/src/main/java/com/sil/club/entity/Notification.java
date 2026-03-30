package com.sil.club.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("notification")
public class Notification {
    @TableId(value = "notify_id", type = IdType.AUTO)
    private Long notifyId;
    
    private Long userId;
    private String content;
    private Integer type;   // 1系统消息, 2活动提醒, 3积分变动
    private Integer isRead; // 0未读, 1已读
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}