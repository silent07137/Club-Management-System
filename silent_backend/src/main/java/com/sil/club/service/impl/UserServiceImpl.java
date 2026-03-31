package com.sil.club.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sil.club.dto.UserDTO;
import com.sil.club.entity.User;
import com.sil.club.mapper.UserMapper;
import com.sil.club.service.IUserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User login(UserDTO userDTO) {
        User dbUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, userDTO.getStudentId()));
        if (dbUser == null || !dbUser.getPassword().equals(userDTO.getPassword())) {
            throw new RuntimeException("学号或密码错误！");
        }
        return dbUser;
    }

    @Override
    public void register(UserDTO userDTO) {
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, userDTO.getStudentId()));
        if (existUser != null) {
            throw new RuntimeException("该学号已被注册！");
        }
        User newUser = new User();
        newUser.setStudentId(userDTO.getStudentId());
        newUser.setPassword(userDTO.getPassword());
        newUser.setName(userDTO.getName());
        newUser.setRole("student");
        newUser.setPoints(0);
        this.save(newUser);
    }
}
