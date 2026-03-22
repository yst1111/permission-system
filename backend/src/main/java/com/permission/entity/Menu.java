package com.permission.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Menu {
    @ApiModelProperty(value = "菜单ID")
    private Long id;
    
    @ApiModelProperty(value = "父级菜单ID")
    private Long parentId;
    
    @ApiModelProperty(value = "菜单名称")
    private String menuName;
    
    @ApiModelProperty(value = "菜单编码")
    private String menuCode;
    
    @ApiModelProperty(value = "菜单路径")
    private String menuPath;
    
    @ApiModelProperty(value = "组件路径")
    private String component;
    
    @ApiModelProperty(value = "菜单图标")
    private String menuIcon;
    
    @ApiModelProperty(value = "菜单类型：1-目录，2-菜单，3-按钮")
    private Integer menuType;
    
    @ApiModelProperty(value = "权限标识")
    private String permission;
    
    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty(value = "排序")
    private Integer sortOrder;
    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    
    @ApiModelProperty(value = "子菜单列表")
    private List<Menu> children;
} 