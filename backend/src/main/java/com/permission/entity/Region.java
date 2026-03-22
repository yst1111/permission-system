package com.permission.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地区实体类
 * 
 * @author system
 * @since 2024-01-01
 */
@ApiModel(description = "地区实体")
public class Region {
    
    /**
     * 地区ID
     */
    @ApiModelProperty(value = "地区ID")
    private Long id;
    
    /**
     * 父级编码
     */
    @ApiModelProperty(value = "父级编码")
    private String parentCode;
    
    /**
     * 地区名称
     */
    @ApiModelProperty(value = "地区名称")
    private String name;
    
    /**
     * 地区名称（与数据库字段对应）
     */
    @ApiModelProperty(value = "地区名称")
    private String regionName;
    
    /**
     * 地区编码
     */
    @ApiModelProperty(value = "地区编码")
    private String code;
    
    /**
     * 地区编码（与数据库字段对应）
     */
    @ApiModelProperty(value = "地区编码")
    private String regionCode;
    
    /**
     * 级别：1-省，2-市，3-区
     */
    @ApiModelProperty(value = "级别：1-省，2-市，3-区")
    private Integer level;
    
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    
    /**
     * 排序（与数据库字段对应）
     */
    @ApiModelProperty(value = "排序")
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用，1-启用
     */
    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    private String status;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    
    /**
     * 子地区列表（用于树形结构）
     */
    @ApiModelProperty(value = "子地区列表（用于树形结构）")
    private List<Region> children;
    
    // 构造方法
    public Region() {}
    
    public Region(Long id, String parentCode, String regionName, String regionCode, Integer level, Integer sortOrder, String status) {
        this.id = id;
        this.parentCode = parentCode;
        this.name = regionName;
        this.regionName = regionName;
        this.code = regionCode;
        this.regionCode = regionCode;
        this.level = level;
        this.sort = sortOrder;
        this.sortOrder = sortOrder;
        this.status = status;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getParentCode() {
        return parentCode;
    }
    
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.regionName = name;
    }
    
    public String getRegionName() {
        return regionName != null ? regionName : name;
    }
    
    public void setRegionName(String regionName) {
        this.regionName = regionName;
        this.name = regionName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
        this.regionCode = code;
    }
    
    public String getRegionCode() {
        return regionCode != null ? regionCode : code;
    }
    
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        this.code = regionCode;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Integer getSort() {
        return sort;
    }
    
    public void setSort(Integer sort) {
        this.sort = sort;
        this.sortOrder = sort;
    }
    
    public Integer getSortOrder() {
        return sortOrder != null ? sortOrder : sort;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        this.sort = sortOrder;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public List<Region> getChildren() {
        return children;
    }
    
    public void setChildren(List<Region> children) {
        this.children = children;
    }
    
    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", parentCode='" + parentCode + '\'' +
                ", name='" + name + '\'' +
                ", regionName='" + regionName + '\'' +
                ", code='" + code + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", level=" + level +
                ", sort=" + sort +
                ", sortOrder=" + sortOrder +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", children=" + children +
                '}';
    }
} 