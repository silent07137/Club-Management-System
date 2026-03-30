package com.sil.club.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sil.club.entity.Notification;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}