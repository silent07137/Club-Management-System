package com.sil.club.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sil.club.entity.Notification;
import com.sil.club.entity.User;
import com.sil.club.service.INotificationService;
import com.sil.club.service.IUserService;
import com.sil.club.utils.AuthUtil;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IUserService userService;

    @GetMapping("/my")
    public Result<List<Notification>> myNotifications(HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        List<Notification> list = notificationService.list(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, currentUser.getUserId())
                .orderByDesc(Notification::getCreateTime));
        return Result.success(list);
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> unreadCount(HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        long count = notificationService.count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, currentUser.getUserId())
                .eq(Notification::getIsRead, 0));
        return Result.success(Map.of("count", count));
    }

    @PostMapping("/read/{id}")
    public Result<String> readOne(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        boolean success = notificationService.update(new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getNotifyId, id)
                .eq(Notification::getUserId, currentUser.getUserId())
                .set(Notification::getIsRead, 1));
        if (success) {
            return Result.success("已标记为已读");
        }
        return Result.error("操作失败，通知可能不存在");
    }

    @PostMapping("/read-all")
    public Result<String> readAll(HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        boolean success = notificationService.update(new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, currentUser.getUserId())
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1));
        if (success) {
            return Result.success("已全部标记为已读");
        }
        return Result.success("没有需要处理的通知");
    }
}
