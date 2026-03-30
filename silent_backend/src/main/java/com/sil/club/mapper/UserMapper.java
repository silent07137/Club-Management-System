package com.sil.club.mapper;

import com.sil.club.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
