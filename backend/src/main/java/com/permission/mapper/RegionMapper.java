package com.permission.mapper;

import com.permission.entity.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地区Mapper接口
 * 
 * @author system
 * @since 2024-01-01
 */
@Mapper
public interface RegionMapper {
    
    /**
     * 查询地区列表
     * 
     * @param region 查询条件
     * @return 地区列表
     */
    List<Region> selectRegionList(Region region);
    
    /**
     * 根据ID查询地区
     * 
     * @param id 地区ID
     * @return 地区信息
     */
    Region selectRegionById(@Param("id") Long id);
    
    /**
     * 根据父级编码查询子地区
     * 
     * @param parentCode 父级编码
     * @return 子地区列表
     */
    List<Region> selectRegionByParentCode(@Param("parentCode") String parentCode);
    
    /**
     * 查询地区树形结构
     * 
     * @return 地区树形列表
     */
    List<Region> selectRegionTree();
    
    /**
     * 新增地区
     * 
     * @param region 地区信息
     * @return 影响行数
     */
    int insertRegion(Region region);
    
    /**
     * 修改地区
     * 
     * @param region 地区信息
     * @return 影响行数
     */
    int updateRegion(Region region);
    
    /**
     * 删除地区
     * 
     * @param id 地区ID
     * @return 影响行数
     */
    int deleteRegionById(@Param("id") Long id);
    
    /**
     * 检查地区编码是否存在
     * 
     * @param regionCode 地区编码
     * @param excludeId 排除的ID（编辑时使用）
     * @return 存在数量
     */
    int checkRegionCodeExists(@Param("regionCode") String regionCode, @Param("excludeId") Long excludeId);
    
    /**
     * 检查是否有子地区
     * 
     * @param id 地区ID
     * @return 子地区数量
     */
    int checkHasChildren(@Param("id") Long id);
} 