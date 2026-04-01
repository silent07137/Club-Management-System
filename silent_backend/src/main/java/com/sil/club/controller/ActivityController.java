package com.sil.club.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.sil.club.entity.Activity;
import com.sil.club.service.IActivityService;
import com.sil.club.utils.Result;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    @GetMapping("/list")
    public Result<List<Activity>> list(@RequestParam(required = false) String title) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        List<Activity> list = activityService.list(wrapper);
        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = activityService.removeById(id);
        if (success) {
            return Result.success("删除成功！");
        } else {
            return Result.error(500, "删除失败，数据可能已不存在");
        }
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Activity activity) {
        boolean success = activityService.save(activity);
        if (success) {
            return Result.success("新增活动成功！");
        } else {
            return Result.error(500, "新增失败，请稍后重试");
        }
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Activity activity) {
        boolean success = activityService.updateById(activity);
        if (success) {
            return Result.success("修改成功！");
        } else {
            return Result.error(500, "修改失败，请稍后重试");
        }
    }

    @GetMapping("/club")
    public Result<List<Activity>> getClubActivities(@RequestParam Long clubId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        // 按社团ID查询，并且按开始时间倒序排列（最新的在前面）
        wrapper.eq(Activity::getClubId, clubId).orderByDesc(Activity::getStartTime);
        List<Activity> list = activityService.list(wrapper);
        return Result.success(list);
    }
}
