package com.sil.club.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sil.club.entity.Club;
import com.sil.club.entity.ClubMember;
import com.sil.club.mapper.ClubMapper;
import com.sil.club.mapper.ClubMemberMapper;
import com.sil.club.service.IClubService;

/**
 * <p>
 * 社团表 服务实现类
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements IClubService {

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClubWithMembers(Long clubId) {
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId);
        clubMemberMapper.delete(wrapper);
        return this.removeById(clubId);
    }
}
