package com.sil.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sil.club.entity.Club;

/**
 * <p>
 * 社团表 服务类
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
public interface IClubService extends IService<Club> {

    // 自定义删除社团及其关联数据的方法
    boolean deleteClubWithMembers(Long clubId);
}
