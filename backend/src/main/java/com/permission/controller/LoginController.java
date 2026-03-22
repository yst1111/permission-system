package com.permission.controller;

import com.permission.entity.User;
import com.permission.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        User user = userMapper.selectUserByUsername(username);
        
        Map<String, Object> result = new HashMap<>();
        
        if (user == null) {
            result.put("code", 401);
            result.put("message", "用户不存在");
            return result;
        }
        
        // 简单密码比对 (MD5)
        String md5Password = org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
        
        if (md5Password.equals(user.getPassword())) {
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", user);
            return result;
        } else {
            result.put("code", 401);
            result.put("message", "密码错误");
            return result;
        }
    }
}
