package com.sil.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 社团表
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Getter
@Setter
public class Club implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 社团ID
     */
    @TableId(value = "club_id", type = IdType.AUTO)
    private Long clubId;

    /**
     * 社团名称
     */
    private String name;

    /**
     * 社团简介
     */
    private String description;

    /**
     * 社长ID
     */
    private Long leaderId;

    /**
     * 状态: 0待审核, 1已通过, 2已拒绝
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
