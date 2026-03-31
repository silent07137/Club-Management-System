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
                .eq(ClubMember::getJoinStatus, 1));

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
            return false;
        }
        if (userId == null || clubId == null) {
            throw new RuntimeException("申请失败：用户信息丢失，请重新登录");
        }
        Long count = this.count(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getClubId, clubId));

        if (count > 0) {
            return false;
        }
        ClubMember newMember = new ClubMember();
        newMember.setUserId(userId);
        newMember.setClubId(clubId);
        newMember.setRoleType(3);
        newMember.setJoinStatus(0);
        return this.save(newMember);
    }

    @Override
    public boolean quitClub(Long clubId, Long userId) {
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId).eq("user_id", userId);
        ClubMember member = this.getOne(wrapper);

        if (member == null) {
            return false;
        }

        if (member.getRoleType() != null && member.getRoleType() == 1) {
            throw new RuntimeException("社长不能退出社团，请直接解散社团");
        }
        return this.remove(wrapper);
    }

    @Override
    public boolean auditMember(Long memberId, Integer status) {
        ClubMember member = this.getById(memberId);
        if (member == null || member.getJoinStatus() != 0) {
            return false;
        }
        member.setJoinStatus(status);
        if (status == 1) {
            member.setRoleType(3);
        }
        return this.updateById(member);
    }
}
