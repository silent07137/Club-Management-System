package com.sil.club.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.dto.UserDTO;
import com.sil.club.entity.User;
import com.sil.club.service.IUserService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.success("注册成功！欢迎加入智慧校园！");
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody UserDTO userDTO) {
        User user = userService.login(userDTO);
        return Result.success("登录成功", user);
    }

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String name) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 如果前端传了姓名，就按姓名模糊搜索
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(User::getName, name);
        }
        
        List<User> list = userService.list(wrapper);
        
        // 🛡️ 安全起见：传给前端之前，把密码字段清空，防止密码泄露
        for (User user : list) {
            user.setPassword(null);
        }
        
        return Result.success(list);
    }
    /**
     * 根据 ID 删除用户（踢出社团）
     * 前端访问路径：DELETE http://localhost:8080/user/delete/1
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 调用 MyBatis-Plus 的 removeById 删除数据库里的记录
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("成员已成功踢出！");
        } else {
            return Result.error(500, "删除失败，该成员可能不存在");
        }
    }
}