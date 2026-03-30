package com.sil.club.service.impl;

import com.sil.club.entity.Activity;
import com.sil.club.mapper.ActivityMapper;
import com.sil.club.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

}
