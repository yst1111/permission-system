package com.permission.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@Api(tags = "首页", description = "首页相关接口")
@Controller
public class HomeController {

    /**
     * 首页
     */
    @ApiOperation(value = "首页", notes = "重定向到API文档")
    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }

    /**
     * 根路径API
     */
    @ApiOperation(value = "根路径API", notes = "获取API服务信息")
    @GetMapping("/api")
    @ResponseBody
    public String apiRoot() {
        return "权限管理系统API服务";
    }

    /**
     * 系统信息
     */
    @ApiOperation(value = "系统信息", notes = "获取系统运行状态信息")
    @GetMapping("/api/info")
    @ResponseBody
    public String systemInfo() {
        return "权限管理系统 v1.0.0 - 运行正常";
    }
} 