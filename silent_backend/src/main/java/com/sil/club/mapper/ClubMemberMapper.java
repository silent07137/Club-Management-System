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

    @Select("SELECT m.member_id AS memberId, m.user_id AS userId, m.club_id AS clubId, " +
            "u.name AS userName, c.name AS clubName, m.create_time AS createTime " +
            "FROM club_member m " +
            "LEFT JOIN user u ON m.user_id = u.user_id " +
            "LEFT JOIN club c ON m.club_id = c.club_id " +
            "WHERE m.join_status = #{status} " +
            "ORDER BY m.create_time DESC")
    List<Map<String, Object>> selectPendingMembersWithClubInfo(@Param("status") Integer status);
}
