package com.sil.club.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // 确认这个路径和你项目一致
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.entity.Club;
import com.sil.club.service.IClubService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/club") // 🚩 注意：这里只写一个 /club
public class ClubController {

    @Autowired
    private IClubService clubService;

    @GetMapping("/list") // 🚩 这个方法必须在大括号里面！
    public Result<List<Club>> list() {
        // 获取 status=1 的社团
        return Result.success(clubService.list(new LambdaQueryWrapper<Club>().eq(Club::getStatus, 1)));
    }
}