package com.permission.controller;

import com.permission.common.Result;
import com.permission.entity.Menu;
import com.permission.service.MenuService;
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
@RequestMapping("/menu")
@Api(tags = "菜单管理", description = "菜单相关接口")
@Validated
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    @ApiOperation(value = "获取菜单列表", notes = "查询菜单列表")
    public Result<List<Menu>> getMenuList(Menu menu) {
        try {
            List<Menu> menus = menuService.getMenuList(menu);
            return Result.success(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取菜单列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/tree")
    @ApiOperation(value = "获取菜单树", notes = "获取菜单树形结构")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> menus = menuService.getMenuTree();
        return Result.success(menus);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取菜单", notes = "根据菜单ID查询菜单信息")
    public Result<Menu> getMenuById(@ApiParam(value = "菜单ID") @PathVariable @NotNull(message = "菜单ID不能为空") Long id) {
        Menu menu = menuService.getMenuById(id);
        return menu != null ? Result.success(menu) : Result.error("菜单不存在");
    }

    @GetMapping("/children/{parentId}")
    @ApiOperation(value = "获取子菜单", notes = "根据父级ID获取子菜单")
    public Result<List<Menu>> getChildrenByParentId(@ApiParam(value = "父级菜单ID") @PathVariable Long parentId) {
        List<Menu> menus = menuService.getChildrenByParentId(parentId);
        return Result.success(menus);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增菜单", notes = "添加新菜单")
    public Result<String> addMenu(@ApiParam(value = "菜单信息") @RequestBody @Valid Menu menu) {
        // 检查菜单编码是否已存在
        if (menuService.getMenuByCode(menu.getMenuName()) != null) {
            return Result.error("菜单名称已存在");
        }
        
        int result = menuService.addMenu(menu);
        return result > 0 ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新菜单", notes = "更新菜单信息")
    public Result<String> updateMenu(@ApiParam(value = "菜单信息") @RequestBody @Valid Menu menu) {
        // 检查菜单是否存在
        Menu existingMenu = menuService.getMenuById(menu.getId());
        if (existingMenu == null) {
            return Result.error("菜单不存在");
        }
        
        int result = menuService.updateMenu(menu);
        return result > 0 ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除菜单", notes = "根据菜单ID删除菜单")
    public Result<String> deleteMenu(@ApiParam(value = "菜单ID") @PathVariable @NotNull(message = "菜单ID不能为空") Long id) {
        // 检查菜单是否存在
        Menu existingMenu = menuService.getMenuById(id);
        if (existingMenu == null) {
            return Result.error("菜单不存在");
        }
        
        // 检查是否有子菜单
        List<Menu> children = menuService.getChildrenByParentId(id);
        if (children != null && !children.isEmpty()) {
            return Result.error("请先删除子菜单");
        }
        
        int result = menuService.deleteMenu(id);
        return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/batch/delete")
    @ApiOperation(value = "批量删除菜单", notes = "批量删除多个菜单")
    public Result<String> batchDeleteMenus(@ApiParam(value = "菜单ID列表") @RequestBody @NotNull(message = "菜单ID列表不能为空") List<Long> ids) {
        if (ids.isEmpty()) {
            return Result.error("菜单ID列表不能为空");
        }
        
        int result = menuService.batchDeleteMenus(ids);
        return result > 0 ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }
}
