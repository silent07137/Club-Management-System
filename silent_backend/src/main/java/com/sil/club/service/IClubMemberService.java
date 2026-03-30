package com.sil.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sil.club.entity.ClubMember;

public interface IClubMemberService extends IService<ClubMember> {
    // 1. 判断用户是否为该社团的管理员（社长或干事）
    boolean isClubAdmin(Long userId, Long clubId);

    // 2. 获取用户在特定社团的角色描述
    String getUserRoleName(Long userId, Long clubId);

    // 3. 处理入社申请
    boolean applyToJoin(Long userId, Long clubId);
}