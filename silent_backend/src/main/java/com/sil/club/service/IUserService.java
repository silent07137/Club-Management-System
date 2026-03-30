package com.sil.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sil.club.dto.UserDTO;
import com.sil.club.entity.User;

public interface IUserService extends IService<User> {
    // 登录方法
    User login(UserDTO userDTO);
    // 注册方法
    void register(UserDTO userDTO);
}