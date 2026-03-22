package com.permission.controller;

import com.permission.common.Result;
import com.permission.entity.User;
import com.permission.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理", description = "用户相关接口")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表", notes = "分页查询用户列表")
    public Result<List<User>> getUserList(@ApiParam(value = "用户查询条件") User user) {
        List<User> users = userService.getUserList(user);
        return Result.success(users);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取用户", notes = "根据用户ID查询用户信息")
    public Result<User> getUserById(@ApiParam(value = "用户ID") @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        User user = userService.getUserById(id);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    @GetMapping("/username/{username}")
    @ApiOperation(value = "根据用户名获取用户", notes = "根据用户名查询用户信息")
    public Result<User> getUserByUsername(@ApiParam(value = "用户名") @PathVariable @NotNull(message = "用户名不能为空") String username) {
        User user = userService.getUserByUsername(username);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增用户", notes = "添加新用户")
    public Result<String> addUser(@ApiParam(value = "用户信息") @RequestBody @Valid User user) {
        // 检查用户名是否已存在
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        int result = userService.addUser(user);
        return result > 0 ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新用户", notes = "更新用户信息")
    public Result<String> updateUser(@ApiParam(value = "用户信息") @RequestBody @Valid User user) {
        // 检查用户是否存在
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        User userWithSameUsername = userService.getUserByUsername(user.getUsername());
        if (userWithSameUsername != null && !userWithSameUsername.getId().equals(user.getId())) {
            return Result.error("用户名已被其他用户使用");
        }
        
        int result = userService.updateUser(user);
        return result > 0 ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    public Result<String> deleteUser(@ApiParam(value = "用户ID") @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        // 检查用户是否存在
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 检查是否为超级管理员（不允许删除）
        if ("admin".equals(existingUser.getUsername())) {
            return Result.error("不能删除超级管理员用户");
        }
        
        int result = userService.deleteUser(id);
        return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/{id}/status")
    @ApiOperation(value = "更新用户状态", notes = "启用或禁用用户")
    public Result<String> updateUserStatus(
            @ApiParam(value = "用户ID") @PathVariable @NotNull(message = "用户ID不能为空") Long id,
            @ApiParam(value = "状态：0-禁用，1-启用") @RequestParam @NotNull(message = "状态不能为空") Integer status) {
        
        if (status != 0 && status != 1) {
            return Result.error("状态值不正确");
        }
        
        // 检查用户是否存在
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许禁用超级管理员
        if ("admin".equals(existingUser.getUsername()) && status == 0) {
            return Result.error("不能禁用超级管理员用户");
        }
        
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        
        int result = userService.updateUser(user);
        return result > 0 ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @PutMapping("/{id}/password")
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码为默认密码")
    public Result<String> resetPassword(@ApiParam(value = "用户ID") @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        // 检查用户是否存在
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        int result = userService.resetPassword(id);
        return result > 0 ? Result.success("密码重置成功") : Result.error("密码重置失败");
    }

    @GetMapping("/{id}/roles")
    @ApiOperation(value = "获取用户角色", notes = "获取用户关联的角色ID列表")
    public Result<List<Long>> getUserRoles(@ApiParam(value = "用户ID") @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }

    @PostMapping("/batch/delete")
    @ApiOperation(value = "批量删除用户", notes = "批量删除多个用户")
    public Result<String> batchDeleteUsers(@ApiParam(value = "用户ID列表") @RequestBody @NotNull(message = "用户ID列表不能为空") List<Long> ids) {
        if (ids.isEmpty()) {
            return Result.error("用户ID列表不能为空");
        }
        
        // 检查是否包含超级管理员
        for (Long id : ids) {
            User user = userService.getUserById(id);
            if (user != null && "admin".equals(user.getUsername())) {
                return Result.error("不能删除超级管理员用户");
            }
        }
        
        int result = userService.batchDeleteUsers(ids);
        return result > 0 ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }
} 