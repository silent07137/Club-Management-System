package com.sil.club.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController; // 确认这个路径和你项目一致

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.entity.Club;
import com.sil.club.entity.ClubMember;
import com.sil.club.mapper.ClubMemberMapper;
import com.sil.club.service.IClubMemberService;
import com.sil.club.service.IClubService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/club")
public class ClubController {

    @Autowired
    private IClubService clubService;
    @Autowired
    private IClubMemberService clubMemberService;
    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @GetMapping("/list")
    public Result<List<Club>> list() {
        // 获取 status=1 的社团
        return Result.success(clubService.list(new LambdaQueryWrapper<Club>().eq(Club::getStatus, 1)));
    }

    // 根据ID查询社团详情
    @GetMapping("/{id}")
    public Result<Club> getById(@PathVariable Long id) {
        // MyBatis-Plus 自带的 getById 方法
        Club club = clubService.getById(id);
        if (club != null) {
            return Result.success(club);
        }
        return Result.error("社团不存在");
    }

    @PostMapping("/apply")
    public Result apply(@RequestBody Club club) {
        club.setStatus(0); // 设置为审核中
        clubService.save(club);
        return Result.success();
    }

    @PostMapping("/approve")
    @Transactional
    public Result approveClub(@RequestBody Map<String, Long> params) {
        Long clubId = params.get("clubId"); // 对应数据库 club_id
        Long userId = params.get("userId"); // 对应数据库 user_id

        // 1. 激活社团
        Club club = clubService.getById(clubId);
        club.setStatus(1); // 已开办
        clubService.updateById(club);

        // 2. 绑定社长
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRoleType(1);   // 1: 社长
        member.setJoinStatus(1); // 1: 已加入
        clubMemberService.save(member);

        return Result.success("审批成功！");
    }

    @GetMapping("/list/pending")
    public Result getPendingClubs() {
        // 查询 status = 0 (审核中) 的记录
        return Result.success(clubService.list(new LambdaQueryWrapper<Club>().eq(Club::getStatus, 0)));
    }

    // ClubController.java
    @PostMapping("/reject")
    public Result rejectClub(@RequestBody Map<String, Object> params) {
        // 1. 获取传入的 clubId
        Long clubId = Long.parseLong(params.get("clubId").toString());
        // (可选) 获取拒绝理由
        String reason = (String) params.get("reason");

        // 2. 更新社团状态为 2 (已驳回)
        Club club = clubService.getById(clubId);
        if (club != null) {
            club.setStatus(2);
            // 如果你的数据库有 reason 字段，可以在此设置：club.setRejectReason(reason);
            clubService.updateById(club);
            return Result.success("已成功驳回该申请");
        }
        return Result.error("未找到该社团记录");
    }

    // 删除社团接口
    @DeleteMapping("/delete/{id}")
    public Result deleteClub(@PathVariable("id") Long id) {
        boolean success = clubService.deleteClubWithMembers(id);
        if (success) {
            return Result.success("社团解散成功");
        } else {
            return Result.error("删除失败，社团可能不存在");
        }
    }

    @GetMapping("/pending")
    public Result getPendingList(@RequestParam Long clubId) {
        // 调用连表查询：状态传 0
        List<Map<String, Object>> list = clubMemberMapper.selectMemberWithUserInfo(clubId, 0);
        return Result.success(list);
    }

    // 审批加入请求
    @PostMapping("/audit")
    public Result auditMember(@RequestBody Map<String, Integer> params) {
        Long memberId = Long.valueOf(params.get("id"));
        Integer status = params.get("status"); // 1-通过，2-拒绝

        boolean success = clubMemberService.auditMember(memberId, status);
        if (success) {
            return Result.success("审批完成");
        } else {
            return Result.error(500, "审批失败，该申请可能已处理");
        }
    }

    @GetMapping("/members")
    public Result getMemberList(@RequestParam Long clubId) {
        // 调用连表查询：状态传 1
        List<Map<String, Object>> list = clubMemberMapper.selectMemberWithUserInfo(clubId, 1);
        return Result.success(list);
    }

    @DeleteMapping("/kick/{memberId}")
    public Result kickMember(@PathVariable("memberId") Long memberId) {
        // 直接删除该成员的关联记录
        boolean success = clubMemberService.removeById(memberId);
        if (success) {
            return Result.success("已将该成员移出社团");
        } else {
            return Result.error(500, "操作失败，成员可能已不存在");
        }
    }
}
