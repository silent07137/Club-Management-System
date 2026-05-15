package com.sil.club.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sil.club.dto.UserDTO;
import com.sil.club.entity.User;
import com.sil.club.service.IClubMemberService;
import com.sil.club.service.IUserService;
import com.sil.club.vo.Result;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IClubMemberService clubMemberService;

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
        user.setPassword(null);
        return Result.success("登录成功", user);
    }

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String name) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(User::getName, name);
        }
        
        List<User> list = userService.list(wrapper);
        
        for (User user : list) {
            user.setPassword(null);
        }
        
        return Result.success(list);
    }
    /**
     * 根据 ID 移出用户的所有社团关系
     * 前端访问路径：DELETE http://localhost:8080/user/remove-from-clubs/1
     * 兼容旧路径 /user/delete/{id}
     */
    @DeleteMapping({"/delete/{id}", "/remove-from-clubs/{id}"})
    public Result<String> removeFromClubs(@PathVariable Long id) {
        boolean success = clubMemberService.removeFromAllClubs(id);
        if (success) {
            return Result.success("已将该用户移出所有社团");
        } else {
            return Result.error(500, "移出失败，该用户可能没有加入任何社团");
        }
    }
}
