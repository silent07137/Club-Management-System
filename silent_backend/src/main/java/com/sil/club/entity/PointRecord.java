package com.sil.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 积分流水表
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Getter
@Setter
@TableName("point_record")
public class PointRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分变动(正负)
     */
    private Integer pointsChange;

    /**
     * 变动原因(如:报名活动/签到)
     */
    private String reason;

    /**
     * 关联业务ID(如活动ID)
     */
    private Long relatedId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
