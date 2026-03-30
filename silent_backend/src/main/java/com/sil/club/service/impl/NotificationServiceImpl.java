package com.sil.club.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sil.club.entity.Notification;
import com.sil.club.mapper.NotificationMapper;
import com.sil.club.service.INotificationService;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {
}