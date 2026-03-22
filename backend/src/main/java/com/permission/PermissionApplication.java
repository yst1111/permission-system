package com.permission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 权限管理系统启动类
 * 
 * @author system
 * @since 2024-01-01
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {"com.permission"})
public class PermissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class, args);
        System.out.println("=================================");
        System.out.println("权限管理系统启动成功！");
        System.out.println("后端服务地址: http://localhost:8080");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("OpenAPI JSON: http://localhost:8080/api-docs");
        System.out.println("=================================");
    }
} 