package com.permission.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统一响应结果类
 * 
 * @author system
 * @since 2024-01-01
 */
@Data
public class Result<T> {
    
    @ApiModelProperty(value = "响应码：200-成功，500-失败")
    private Integer code;
    
    @ApiModelProperty(value = "响应消息")
    private String message;
    
    @ApiModelProperty(value = "响应数据")
    private T data;
    
    @ApiModelProperty(value = "时间戳")
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    
    /**
     * 失败响应（自定义状态码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
} 