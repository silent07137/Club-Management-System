package com.sil.club.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.entity.Club; // 确保这里的包路径和你项目里的一致
import com.sil.club.entity.ClubMember;
import com.sil.club.entity.Notification;
import com.sil.club.service.IClubMemberService;
import com.sil.club.service.IClubService;
import com.sil.club.service.INotificationService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/club/member")
public class ClubMemberController {

    @Autowired
    private IClubMemberService clubMemberService;

    @Autowired
    private IClubService clubService;

    @Autowired
    private INotificationService notificationService;

    /**
     * 1. 提交入社申请 前端传参：{ "userId": 1, "clubId": 10 }
     */
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody ClubMember clubMember) {
        boolean success = clubMemberService.applyToJoin(clubMember.getUserId(), clubMember.getClubId());

        if (success) {
            Club club = clubService.getById(clubMember.getClubId());
            if (club != null && club.getLeaderId() != null) {
                Notification note = new Notification();
                note.setUserId(club.getLeaderId());
                note.setContent("有人申请加入您的社团【" + club.getName() + "】，请尽快审批！");
                note.setType(1);
                notificationService.save(note);
            }
            return Result.success("申请成功，请耐心等待社长审核");
        } else {
            return Result.error("申请失败：您可能已在社团中或有未处理的申请");
        }
    }

    @PostMapping("/audit")
    public Result<String> audit(@RequestBody Map<String, Object> params) {
        Object memberIdObj = params.get("memberId");
        Object statusObj = params.get("joinStatus");
        if (memberIdObj == null || statusObj == null) {
            return Result.error("审批失败：memberId 或 joinStatus 缺失");
        }

        Long memberId = Long.valueOf(memberIdObj.toString());
        Integer status = Integer.valueOf(statusObj.toString());

        ClubMember member = clubMemberService.getById(memberId);
        if (member == null) {
            return Result.error("申请记录不存在");
        }

        member.setJoinStatus(status);
        clubMemberService.updateById(member);

        Notification note = new Notification();
        note.setUserId(member.getUserId());
        String resultText = (status.equals(1)) ? "恭喜！您的入社申请已通过！" : "很抱歉，您的入社申请被拒绝了。";
        note.setContent(resultText);
        notificationService.save(note);

        return Result.success("处理完成");
    }

    @GetMapping("/list/pending")
    public Result<List<ClubMember>> getPendingList() {
        List<ClubMember> list = clubMemberService.list(
                new LambdaQueryWrapper<ClubMember>().eq(ClubMember::getJoinStatus, 0)
        );
        return Result.success(list);
    }

    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyClubs(@RequestParam Long userId) {
        List<ClubMember> members = clubMemberService.list(
                new LambdaQueryWrapper<ClubMember>()
                        .eq(ClubMember::getUserId, userId)
                        .eq(ClubMember::getJoinStatus, 1)
        );

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ClubMember m : members) {
            Map<String, Object> map = new HashMap<>();
            Club club = clubService.getById(m.getClubId());
            map.put("clubId", m.getClubId());
            map.put("memberId", m.getMemberId());
            map.put("clubName", club != null ? club.getName() : "未知社团");
            map.put("roleType", m.getRoleType());
            map.put("createTime", m.getCreateTime());
            resultList.add(map);
        }
        return Result.success(resultList);
    }

    // 退出社团接口
    @DeleteMapping("/quit")
    public Result quitClub(@RequestParam Long clubId, @RequestParam Long userId) {
        boolean success = clubMemberService.quitClub(clubId, userId);
        if (success) {
            return Result.success("已成功退出该社团");
        } else {
            return Result.error("退出失败，您可能不在此社团中");
        }
    }
}
