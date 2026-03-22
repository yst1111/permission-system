package com.permission.controller;

import com.permission.common.Result;
import com.permission.entity.Role;
import com.permission.service.RoleService;
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
@RequestMapping("/role")
@Api(tags = "角色管理", description = "角色相关接口")
@Validated
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @ApiOperation(value = "获取角色列表", notes = "查询角色列表")
    public Result<List<Role>> getRoleList(Role role) {
        List<Role> roles = roleService.getRoleList(role);
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取角色", notes = "根据角色ID查询角色信息")
    public Result<Role> getRoleById(@ApiParam(value = "角色ID") @PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        Role role = roleService.getRoleById(id);
        return role != null ? Result.success(role) : Result.error("角色不存在");
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增角色", notes = "添加新角色")
    public Result<String> addRole(@ApiParam(value = "角色信息") @RequestBody @Valid Role role) {
        // 检查角色编码是否已存在
        if (roleService.getRoleByCode(role.getRoleCode()) != null) {
            return Result.error("角色编码已存在");
        }
        
        int result = roleService.addRole(role);
        return result > 0 ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新角色", notes = "更新角色信息")
    public Result<String> updateRole(@ApiParam(value = "角色信息") @RequestBody @Valid Role role) {
        // 检查角色是否存在
        Role existingRole = roleService.getRoleById(role.getId());
        if (existingRole == null) {
            return Result.error("角色不存在");
        }
        
        // 检查角色编码是否被其他角色使用
        Role roleWithSameCode = roleService.getRoleByCode(role.getRoleCode());
        if (roleWithSameCode != null && !roleWithSameCode.getId().equals(role.getId())) {
            return Result.error("角色编码已被其他角色使用");
        }
        
        int result = roleService.updateRole(role);
        return result > 0 ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色", notes = "根据角色ID删除角色")
    public Result<String> deleteRole(@ApiParam(value = "角色ID") @PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        // 检查角色是否存在
        Role existingRole = roleService.getRoleById(id);
        if (existingRole == null) {
            return Result.error("角色不存在");
        }
        
        int result = roleService.deleteRole(id);
        return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/batch/delete")
    @ApiOperation(value = "批量删除角色", notes = "批量删除多个角色")
    public Result<String> batchDeleteRoles(@ApiParam(value = "角色ID列表") @RequestBody @NotNull(message = "角色ID列表不能为空") List<Long> ids) {
        if (ids.isEmpty()) {
            return Result.error("角色ID列表不能为空");
        }
        
        int result = roleService.batchDeleteRoles(ids);
        return result > 0 ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }

    @GetMapping("/{id}/menus")
    @ApiOperation(value = "获取角色菜单", notes = "获取角色关联的菜单ID列表")
    public Result<List<Long>> getRoleMenus(@ApiParam(value = "角色ID") @PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }

    @PutMapping("/{id}/menus")
    @ApiOperation(value = "分配菜单", notes = "为角色分配菜单权限")
    public Result<String> assignMenus(
            @ApiParam(value = "角色ID") @PathVariable @NotNull(message = "角色ID不能为空") Long id,
            @ApiParam(value = "菜单ID列表") @RequestBody List<Long> menuIds) {
        
        // 检查角色是否存在
        Role existingRole = roleService.getRoleById(id);
        if (existingRole == null) {
            return Result.error("角色不存在");
        }
        
        int result = roleService.assignMenus(id, menuIds);
        return result > 0 ? Result.success("菜单分配成功") : Result.error("菜单分配失败");
    }
}
