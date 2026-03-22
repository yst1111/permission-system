package com.permission.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/test")
@Api(tags = "测试接口", description = "用于测试的接口")
public class TestController {

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    @ApiOperation(value = "测试接口", notes = "返回Hello World消息")
    public String hello() {
        return "Hello World! 权限管理系统运行正常";
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ApiOperation(value = "健康检查", notes = "检查系统运行状态")
    public String health() {
        return "OK";
    }
} 