package com.sil.club.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sil.club.entity.ClubMember;
import com.sil.club.mapper.ClubMemberMapper;
import com.sil.club.service.IClubMemberService;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements IClubMemberService {

    /**
     * 判断用户是否拥有该社团的管理权限 逻辑：role_type 为 1(社长) 或 2(干事)
     */
    @Override
    public boolean isClubAdmin(Long userId, Long clubId) {
        ClubMember member = this.getOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getJoinStatus, 1)); // 必须是已加入状态

        return member != null && (member.getRoleType() == 1 || member.getRoleType() == 2);
    }

    /**
     * 获取用户在社团内的身份标签
     */
    @Override
    public String getUserRoleName(Long userId, Long clubId) {
        ClubMember member = this.getOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getClubId, clubId));

        if (member == null) {
            return "非成员";
        }
        if (member.getJoinStatus() == 0) {
            return "申请中";
        }

        return switch (member.getRoleType()) {
            case 1 ->
                "社长";
            case 2 ->
                "干事";
            case 3 ->
                "普通成员";
            default ->
                "未知";
        };
    }

    /**
     * 提交入社申请
     */
    @Override
    public boolean applyToJoin(Long userId, Long clubId) {
        System.out.println("收到申请：用户ID=" + userId + ", 社团ID=" + clubId);
        if (userId == null) {
            return false; // 或者抛出异常
        }
        if (userId == null || clubId == null) {
            throw new RuntimeException("申请失败：用户信息丢失，请重新登录");
        }
        // 1. 检查是否重复申请
        Long count = this.count(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getClubId, clubId));

        if (count > 0) {
            return false; // 已经有记录了，不能再投
        }
        // 2. 插入新记录，初始状态为 0 (申请中)
        ClubMember newMember = new ClubMember();
        newMember.setUserId(userId);
        newMember.setClubId(clubId);
        newMember.setRoleType(3);    // 默认进场都是普通成员
        newMember.setJoinStatus(0);  // 等待审批

        return this.save(newMember);
    }

    @Override
    public boolean quitClub(Long clubId, Long userId) {
        // 1. 先查出当前用户在这个社团的成员信息
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId).eq("user_id", userId);
        ClubMember member = this.getOne(wrapper);

        if (member == null) {
            return false; // 根本不在社团里
        }

        // 2. 核心校验：如果是社长（假设 roleType == 1 代表社长），禁止退出
        if (member.getRoleType() != null && member.getRoleType() == 1) {
            throw new RuntimeException("社长不能退出社团，请直接解散社团");
            // 如果你的项目有统一的业务异常类 (如 BusinessException)，建议抛出你们自定义的异常
        }

        // 3. 普通成员，正常删除记录
        return this.remove(wrapper);
    }
}
