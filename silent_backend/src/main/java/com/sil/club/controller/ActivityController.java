package com.sil.club.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.entity.User;
import com.sil.club.entity.Activity;
import com.sil.club.service.IActivityService;
import com.sil.club.service.IClubMemberService;
import com.sil.club.service.IUserService;
import com.sil.club.vo.Result;
import com.sil.club.utils.AuthUtil;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IClubMemberService clubMemberService;

    @GetMapping("/list")
    public Result<List<Activity>> list(@RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        AuthUtil.requireAdmin(request, userService);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        List<Activity> list = activityService.list(wrapper);
        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error(500, "删除失败，数据可能已不存在");
        }
        requireActivityOwnerOrAdmin(request, activity.getClubId());
        boolean success = activityService.removeById(id);
        if (success) {
            return Result.success("删除成功！");
        } else {
            return Result.error(500, "删除失败，数据可能已不存在");
        }
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Activity activity, HttpServletRequest request) {
        requireActivityOwnerOrAdmin(request, activity.getClubId());
        boolean success = activityService.save(activity);
        if (success) {
            return Result.success("新增活动成功！");
        } else {
            return Result.error(500, "新增失败，请稍后重试");
        }
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Activity activity, HttpServletRequest request) {
        requireActivityOwnerOrAdmin(request, activity.getClubId());
        boolean success = activityService.updateById(activity);
        if (success) {
            return Result.success("修改成功！");
        } else {
            return Result.error(500, "修改失败，请稍后重试");
        }
    }

    @PutMapping("/status")
    public Result<String> updateStatus(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Object idObj = params.get("id");
        Object statusObj = params.get("status");
        if (idObj == null || statusObj == null) {
            return Result.error("参数缺失");
        }

        Long id = Long.valueOf(idObj.toString());
        Integer status = Integer.valueOf(statusObj.toString());
        if (status < 0 || status > 2) {
            return Result.error("状态值不合法");
        }

        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        requireActivityOwnerOrAdmin(request, activity.getClubId());
        activity.setStatus(status);
        boolean success = activityService.updateById(activity);
        if (success) {
            return Result.success("状态已更新");
        }
        return Result.error("状态更新失败");
    }

    @GetMapping("/club")
    public Result<List<Activity>> getClubActivities(@RequestParam Long clubId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        // 按社团ID查询，并且按开始时间倒序排列（最新的在前面）
        wrapper.eq(Activity::getClubId, clubId).orderByDesc(Activity::getStartTime);
        List<Activity> list = activityService.list(wrapper);
        return Result.success(list);
    }

    private void requireActivityOwnerOrAdmin(HttpServletRequest request, Long clubId) {
        User currentUser = AuthUtil.requireCurrentUser(request, userService);
        boolean isAdmin = Integer.valueOf(0).equals(currentUser.getGlobalRole())
                || "ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole());
        if (isAdmin) {
            return;
        }

        if (clubId == null) {
            throw new com.sil.club.exception.AuthException(403, "该操作仅允许管理员或社团管理者执行");
        }

        boolean isClubAdmin = clubMemberService.isClubAdmin(currentUser.getUserId(), clubId);
        if (!isClubAdmin) {
            throw new com.sil.club.exception.AuthException(403, "该操作仅允许管理员或社团管理者执行");
        }
    }
}
