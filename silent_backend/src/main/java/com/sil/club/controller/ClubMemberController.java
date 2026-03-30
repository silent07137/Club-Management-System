package com.sil.club.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
        // 调用我们刚才在 Service 写的 applyToJoin 逻辑
        boolean success = clubMemberService.applyToJoin(clubMember.getUserId(), clubMember.getClubId());

        if (success) {
            // 【联动】给社长发个通知
            Club club = clubService.getById(clubMember.getClubId());
            if (club != null && club.getLeaderId() != null) {
                Notification note = new Notification();
                note.setUserId(club.getLeaderId()); // 接收人是社长
                note.setContent("有人申请加入您的社团【" + club.getName() + "】，请尽快审批！");
                note.setType(1); // 系统消息
                notificationService.save(note);
            }
            return Result.success("申请成功，请耐心等待社长审核");
        } else {
            return Result.error("申请失败：您可能已在社团中或有未处理的申请");
        }
    }

    /**
     * 2. 社长审批申请 (顺手把这个也写了，逻辑才完整) 前端传参：{ "memberId": 100, "status": 1 } // 1-同意,
     * 2-拒绝
     */
    @PostMapping("/audit")
    public Result<String> audit(@RequestBody Map<String, Object> params) {
        // 🚩 修复 1：将 "status" 改为与前端一致的 "joinStatus"
        Object memberIdObj = params.get("memberId");
        Object statusObj = params.get("joinStatus");

        // 🚩 修复 2：增加空值检查，防止 NullPointerException
        if (memberIdObj == null || statusObj == null) {
            return Result.error("审批失败：memberId 或 joinStatus 缺失");
        }

        Long memberId = Long.valueOf(memberIdObj.toString());
        Integer status = Integer.valueOf(statusObj.toString());

        ClubMember member = clubMemberService.getById(memberId);
        if (member == null) {
            return Result.error("申请记录不存在");
        }

        // 更新状态
        member.setJoinStatus(status);
        clubMemberService.updateById(member);

        // 【联动】给申请人发个结果通知
        Notification note = new Notification();
        note.setUserId(member.getUserId());
        // 🚩 修复 3：使用更加安全的比较方式
        String resultText = (status.equals(1)) ? "恭喜！您的入社申请已通过！" : "很抱歉，您的入社申请被拒绝了。";
        note.setContent(resultText);
        notificationService.save(note);

        return Result.success("处理完成");
    }

    @GetMapping("/list/pending")
    public Result<List<ClubMember>> getPendingList() {
        // 🚩 查询所有 join_status = 0 (申请中) 的记录
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

            // 🚩 关键修复：一定要把 clubId 存进去！
            map.put("clubId", m.getClubId());

            map.put("memberId", m.getMemberId());
            map.put("clubName", club != null ? club.getName() : "未知社团");
            map.put("roleType", m.getRoleType());
            map.put("createTime", m.getCreateTime());
            resultList.add(map);
        }
        return Result.success(resultList);
    }
}
