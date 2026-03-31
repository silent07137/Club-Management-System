package com.sil.club.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sil.club.entity.ClubMember;

@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {

    @Select("SELECT m.*, u.name as name, u.avatar " +
            "FROM club_member m " +
            "LEFT JOIN user u ON m.user_id = u.user_id " +
            "WHERE m.club_id = #{clubId} AND m.join_status = #{status}")
    List<Map<String, Object>> selectMemberWithUserInfo(@Param("clubId") Long clubId, @Param("status") Integer status);
}