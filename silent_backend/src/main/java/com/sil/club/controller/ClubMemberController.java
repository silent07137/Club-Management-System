package com.sil.club.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 1. 提交入社申请
     * 前端传参：{ "userId": 1, "clubId": 10 }
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
     * 2. 社长审批申请 (顺手把这个也写了，逻辑才完整)
     * 前端传参：{ "memberId": 100, "status": 1 }  // 1-同意, 2-拒绝
     */
    @PostMapping("/audit")
    public Result<String> audit(@RequestBody Map<String, Object> params) {
        Long memberId = Long.valueOf(params.get("memberId").toString());
        Integer status = (Integer) params.get("status");

        ClubMember member = clubMemberService.getById(memberId);
        if (member == null) return Result.error("申请记录不存在");

        // 更新状态
        member.setJoinStatus(status);
        clubMemberService.updateById(member);

        // 【联动】给申请人发个结果通知
        Notification note = new Notification();
        note.setUserId(member.getUserId());
        String resultText = (status == 1) ? "恭喜！您的入社申请已通过！" : "很抱歉，您的入社申请被拒绝了。";
        note.setContent(resultText);
        notificationService.save(note);

        return Result.success("处理完成");
    }
}