package com.sil.club.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sil.club.entity.ClubMember;

@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {
    // MyBatis-Plus 会自动生成基础的 CRUD 语句
}