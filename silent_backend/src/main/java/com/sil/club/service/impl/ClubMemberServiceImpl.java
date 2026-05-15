package com.sil.club.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sil.club.entity.Club;
import com.sil.club.entity.ClubMember;
import com.sil.club.mapper.ClubMemberMapper;
import com.sil.club.service.IClubService;
import com.sil.club.service.IClubMemberService;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements IClubMemberService {

    private final IClubService clubService;

    public ClubMemberServiceImpl(IClubService clubService) {
        this.clubService = clubService;
    }

    /**
     * 判断用户是否拥有该社团的管理权限 逻辑：role_type 为 1(社长) 或 2(干事)
     */
    @Override
    public boolean isClubAdmin(Long userId, Long clubId) {
        ClubMember member = this.getOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getJoinStatus, 1));

        return member != null
                && (Integer.valueOf(1).equals(member.getRoleType())
                        || Integer.valueOf(2).equals(member.getRoleType()));
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
        if (member.getRoleType() == null) {
            return "未知";
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

        if (Integer.valueOf(1).equals(member.getRoleType())) {
            throw new RuntimeException("社长不能退出社团，请直接解散社团");
        }
        return this.remove(wrapper);
    }

    @Override
    public boolean auditMember(Long memberId, Integer status) {
        if (status == null || (status != 1 && status != 2)) {
            return false;
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromAllClubs(Long userId) {
        if (userId == null) {
            return false;
        }

        Long leaderCount = clubService.count(new LambdaQueryWrapper<Club>()
                .eq(Club::getLeaderId, userId));
        if (leaderCount != null && leaderCount > 0) {
            throw new RuntimeException("该用户是社长，请先解散社团或转让社长职位");
        }

        Long membershipCount = this.count(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId));
        if (membershipCount == null || membershipCount == 0) {
            return false;
        }

        return this.remove(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId));
    }
}
