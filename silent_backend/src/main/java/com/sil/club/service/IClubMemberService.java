package com.sil.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sil.club.entity.ClubMember;

public interface IClubMemberService extends IService<ClubMember> {
    boolean isClubAdmin(Long userId, Long clubId);

    String getUserRoleName(Long userId, Long clubId);

    boolean applyToJoin(Long userId, Long clubId);

    boolean quitClub(Long clubId, Long userId);

    boolean auditMember(Long memberId, Integer status);
}