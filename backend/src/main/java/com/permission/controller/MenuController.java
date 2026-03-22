package com.permission.controller;

import com.permission.entity.User;
import com.permission.mapper.UserMapper;
import com.permission.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class MenuController {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MenuMapper menuMapper;

    @GetMapping("/menus")
    public Map<String, Object> getUserMenus(@RequestParam String username) {
        User user = userMapper.selectUserByUsername(username);
        Map<String, Object> result = new HashMap<>();
        
        if (user == null) {
            result.put("code", 401);
            result.put("message", "用户不存在");
            return result;
        }
        
        // 获取用户的角色ID
        List<Long> roleIds = userMapper.selectUserRoleIds(user.getId());
        
        if (roleIds == null || roleIds.isEmpty()) {
            result.put("code", 200);
            result.put("data", List.of());
            return result;
        }
        
        // 获取角色的菜单
        List<Map<String, Object>> menus = menuMapper.selectMenusByRoleIds(roleIds);
        
        result.put("code", 200);
        result.put("data", menus);
        return result;
    }
}
