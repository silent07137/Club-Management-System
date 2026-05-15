package com.sil.club.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("club")
public class Club {
    @TableId(value = "club_id", type = IdType.AUTO)
    private Long clubId;

    private String name;        // 对应数据库 name
    private String description; // 对应数据库 description
    private Long leaderId;      // 对应数据库 leader_id (社长ID)
    private Integer status;     // 对应数据库 status (1-正常, 0-关闭)
    @TableField("reject_reason")
    private String rejectReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
