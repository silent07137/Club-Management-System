package com.sil.club.service.impl;

import com.sil.club.entity.Registration;
import com.sil.club.mapper.RegistrationMapper;
import com.sil.club.service.IRegistrationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 报名记录表 服务实现类
 * </p>
 *
 * @author sil
 * @since 2026-03-29
 */
@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements IRegistrationService {

}
