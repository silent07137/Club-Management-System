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
@RequestMapping("/activity") // 这里的路径要和前端 request.get('/activity/list') 对应
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    /**
     * 获取所有活动列表
     * 前端访问路径：GET http://localhost:8080/activity/list
     */
    @GetMapping("/list")
    public Result<List<Activity>> list(@RequestParam(required = false) String title) {
        // 1. 创建一个查询条件包装器 (MyBatis-Plus 的神器)
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        // 2. 如果前端传了 title 过来，我们就加上 LIKE 模糊查询条件
        if (title != null && !title.trim().isEmpty()) {
            // 这相当于 SQL: WHERE title LIKE '%歌手%'
            wrapper.like(Activity::getTitle, title);
        }
        
        // 3. 按照条件查询数据
        List<Activity> list = activityService.list(wrapper);
        return Result.success(list);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 调用 MyBatis-Plus 提供的 removeById 方法直接删除数据库数据
        boolean success = activityService.removeById(id);
        if (success) {
            return Result.success("删除成功！");
        } else {
            return Result.error(500, "删除失败，数据可能已不存在");
        }
    }
    @PostMapping("/add")
    public Result<String> add(@RequestBody Activity activity) {
        // 调用 MyBatis-Plus 提供的 save 方法存入数据库
        boolean success = activityService.save(activity);
        if (success) {
            return Result.success("新增活动成功！");
        } else {
            return Result.error(500, "新增失败，请稍后重试");
        }
    }
    /**
     * 修改活动
     * 前端访问路径：PUT http://localhost:8080/activity/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Activity activity) {
        // 调用 MyBatis-Plus 提供的 updateById 方法，根据传入的 ID 修改对应的数据
        boolean success = activityService.updateById(activity);
        if (success) {
            return Result.success("修改成功！");
        } else {
            return Result.error(500, "修改失败，请稍后重试");
        }
    }
}