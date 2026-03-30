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
        // 1. 去数据库里根据学号查询用户
        User dbUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, userDTO.getStudentId()));
        
        // 2. 判断用户是否存在，以及密码是否正确（现阶段为了演示先用明文对比，后期我们可以加上 MD5 加密）
        if (dbUser == null || !dbUser.getPassword().equals(userDTO.getPassword())) {
            // 如果账号密码不对，直接抛出异常，咱们的“全局异常处理器”会接住它！
            throw new RuntimeException("学号或密码错误！");
        }
        return dbUser;
    }

    @Override
    public void register(UserDTO userDTO) {
        // 1. 检查学号是否已经被注册过
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, userDTO.getStudentId()));
        if (existUser != null) {
            throw new RuntimeException("该学号已被注册！");
        }

        // 2. 创建新用户对象，把前端传来的数据塞进去，然后存入数据库
        User newUser = new User();
        newUser.setStudentId(userDTO.getStudentId());
        newUser.setPassword(userDTO.getPassword());
        newUser.setName(userDTO.getName());
        newUser.setRole("student"); // 默认注册为普通学生
        newUser.setPoints(0); // 初始积分为0
        
        this.save(newUser); // MyBatis-Plus 提供的神级方法，直接插入数据库！
    }
}