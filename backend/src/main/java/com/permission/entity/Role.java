package com.permission.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    @ApiModelProperty(value = "角色ID")
    private Long id;
    
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    
    @ApiModelProperty(value = "角色描述")
    private String description;
    
    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty(value = "排序")
    private Integer sortOrder;
    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
} 