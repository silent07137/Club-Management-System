package com.sil.club.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("club_member")
public class ClubMember {
    @TableId(value = "member_id", type = IdType.AUTO)
    private Long memberId;
    
    private Long clubId;
    private Long userId;
    
    private Integer roleType;   // 1社长, 2管理组, 3普通成员
    private Integer joinStatus; // 0申请中, 1已加入, 2已拒绝
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}