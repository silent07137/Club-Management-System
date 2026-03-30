package com.sil.club.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sil.club.service.IActivityService;
import com.sil.club.service.IUserService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/stats")
public class StatsController {

    // 把用户和活动的管家都请过来
    @Autowired
    private IUserService userService;

    @Autowired
    private IActivityService activityService;

    /**
     * 获取系统首页统计数据
     * 前端访问路径：GET http://localhost:8080/stats/info
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> data = new HashMap<>();
        
        // 调用 MyBatis-Plus 自带的 count() 方法，直接统计数据库里有几条数据
        data.put("userCount", userService.count());
        data.put("activityCount", activityService.count());
        
        return Result.success(data);
    }
}