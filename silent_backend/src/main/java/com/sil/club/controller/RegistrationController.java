package com.sil.club.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.entity.Activity;
import com.sil.club.entity.Notification;
import com.sil.club.entity.PointRecord;
import com.sil.club.entity.Registration;
import com.sil.club.entity.User;
import com.sil.club.service.IActivityService;
import com.sil.club.service.IClubMemberService;
import com.sil.club.service.INotificationService;
import com.sil.club.service.IPointRecordService;
import com.sil.club.service.IRegistrationService;
import com.sil.club.service.IUserService;
import com.sil.club.utils.AuthUtil;
import com.sil.club.vo.Result;

/**
 * <p>
 * 报名记录表 前端控制器
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@RestController
@RequestMapping("/club/registration")
public class RegistrationController {

    @Autowired
    private IRegistrationService registrationService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IClubMemberService clubMemberService;

    @Autowired
    private IPointRecordService pointRecordService;

    @Autowired
    private INotificationService notificationService;

    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myRegistrations(@RequestParam Long clubId, HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        if (!hasClubAccess(currentUser.getUserId(), clubId)) {
            return Result.success(new ArrayList<>());
        }

        List<Activity> activities = activityService.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getClubId, clubId));
        if (activities.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> activityIds = activities.stream()
                .map(Activity::getActivityId)
                .collect(Collectors.toList());

        List<Registration> registrations = registrationService.list(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, currentUser.getUserId())
                .in(Registration::getActivityId, activityIds));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Registration registration : registrations) {
            Map<String, Object> map = new HashMap<>();
            map.put("activityId", registration.getActivityId());
            map.put("status", registration.getStatus());
            map.put("regTime", registration.getRegTime());
            map.put("signTime", registration.getSignTime());
            result.add(map);
        }
        return Result.success(result);
    }

    @PostMapping("/apply")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> apply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long activityId = parseLong(params.get("activityId"));
        if (activityId == null) {
            return Result.error("参数缺失");
        }

        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        if (!hasClubAccess(currentUser.getUserId(), activity.getClubId())) {
            return Result.error(403, "请先加入该社团");
        }

        Registration existing = getRegistration(currentUser.getUserId(), activityId);
        if (existing != null) {
            if (Integer.valueOf(1).equals(existing.getStatus()) || Integer.valueOf(3).equals(existing.getStatus())) {
                return Result.error("您已经报名或已签到");
            }
            existing.setStatus(1);
            existing.setRegTime(LocalDateTime.now());
            existing.setSignTime(null);
            registrationService.updateById(existing);
        } else {
            Registration registration = new Registration();
            registration.setActivityId(activityId);
            registration.setUserId(currentUser.getUserId());
            registration.setStatus(1);
            registration.setRegTime(LocalDateTime.now());
            registrationService.save(registration);
        }

        Notification note = new Notification();
        note.setUserId(currentUser.getUserId());
        note.setType(2);
        note.setIsRead(0);
        note.setContent("您已成功报名活动【" + activity.getTitle() + "】。");
        notificationService.save(note);

        return Result.success("报名成功");
    }

    @DeleteMapping("/cancel")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cancel(@RequestParam Long activityId, HttpServletRequest request) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        Registration registration = getRegistration(currentUser.getUserId(), activityId);
        if (registration == null || !Integer.valueOf(1).equals(registration.getStatus())) {
            return Result.error("当前没有可取消的报名记录");
        }

        registration.setStatus(2);
        registrationService.updateById(registration);
        return Result.success("报名已取消");
    }

    @PostMapping("/sign-in")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> signIn(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long activityId = parseLong(params.get("activityId"));
        if (activityId == null) {
            return Result.error("参数缺失");
        }

        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        if (!hasClubAccess(currentUser.getUserId(), activity.getClubId())) {
            return Result.error(403, "请先加入该社团");
        }

        Registration registration = getRegistration(currentUser.getUserId(), activityId);
        if (registration == null) {
            return Result.error("请先报名后再签到");
        }
        if (Integer.valueOf(3).equals(registration.getStatus())) {
            return Result.success("您已经签到过了");
        }
        if (!Integer.valueOf(1).equals(registration.getStatus())) {
            return Result.error("请先报名后再签到");
        }

        registration.setStatus(3);
        registration.setSignTime(LocalDateTime.now());
        registrationService.updateById(registration);

        int reward = activity.getPointsReward() == null ? 0 : activity.getPointsReward();
        if (reward != 0) {
            User user = userService.getById(currentUser.getUserId());
            if (user != null) {
                user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + reward);
                userService.updateById(user);
            }

            PointRecord record = new PointRecord();
            record.setUserId(currentUser.getUserId());
            record.setPointsChange(reward);
            record.setReason("活动签到：" + activity.getTitle());
            record.setRelatedId(activityId);
            pointRecordService.save(record);
        }

        Notification note = new Notification();
        note.setUserId(currentUser.getUserId());
        note.setType(3);
        note.setIsRead(0);
        note.setContent("您已完成活动【" + activity.getTitle() + "】签到，获得 +" + reward + " 积分。");
        notificationService.save(note);

        return Result.success("签到成功");
    }

    private boolean hasClubAccess(Long userId, Long clubId) {
        if (userId == null || clubId == null) {
            return false;
        }
        boolean isAdmin = clubMemberService.isClubAdmin(userId, clubId);
        if (isAdmin) {
            return true;
        }
        return clubMemberService.count(new LambdaQueryWrapper<com.sil.club.entity.ClubMember>()
                .eq(com.sil.club.entity.ClubMember::getUserId, userId)
                .eq(com.sil.club.entity.ClubMember::getClubId, clubId)
                .eq(com.sil.club.entity.ClubMember::getJoinStatus, 1)) > 0;
    }

    private Registration getRegistration(Long userId, Long activityId) {
        return registrationService.getOne(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getActivityId, activityId));
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
