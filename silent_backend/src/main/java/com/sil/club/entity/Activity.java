package com.sil.club.entity;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 活动表
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Getter
@Setter
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    @TableId(value = "activity_id", type = IdType.AUTO)
    private Long activityId;

    /**
     * 所属社团ID
     */
    private Long clubId;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动详情
     */
    private String description;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 人数上限(0为不限)
     */
    private Integer maxParticipants;

    /**
     * 状态: 0未开始, 1进行中, 2已结束
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;


}
