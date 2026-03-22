package com.permission.service;

import com.permission.entity.Region;
import java.util.List;

/**
 * 地区服务接口
 * 
 * @author system
 * @since 2024-01-01
 */
public interface RegionService {
    
    /**
     * 查询地区列表
     * 
     * @param region 查询条件
     * @return 地区列表
     */
    List<Region> getRegionList(Region region);
    
    /**
     * 根据ID查询地区
     * 
     * @param id 地区ID
     * @return 地区信息
     */
    Region getRegionById(Long id);
    
    /**
     * 查询地区树形结构
     * 
     * @return 地区树形列表
     */
    List<Region> getRegionTree();
    
    /**
     * 新增地区
     * 
     * @param region 地区信息
     * @return 是否成功
     */
    boolean addRegion(Region region);
    
    /**
     * 修改地区
     * 
     * @param region 地区信息
     * @return 是否成功
     */
    boolean updateRegion(Region region);
    
    /**
     * 删除地区
     * 
     * @param id 地区ID
     * @return 是否成功
     */
    boolean deleteRegion(Long id);
    
    /**
     * 检查地区编码是否存在
     * 
     * @param regionCode 地区编码
     * @param excludeId 排除的ID（编辑时使用）
     * @return 是否存在
     */
    boolean isRegionCodeExists(String regionCode, Long excludeId);
    
    /**
     * 批量导入地区数据
     * 
     * @param regions 地区数据列表
     * @return 导入结果
     */
    String importRegions(List<Region> regions);
    
    /**
     * 根据父级编码获取子地区数量
     * 
     * @param parentCode 父级编码
     * @return 子地区数量
     */
    int getChildrenCount(String parentCode);
    
    /**
     * 获取地区路径（从根到当前节点的完整路径）
     * 
     * @param regionId 地区ID
     * @return 地区路径
     */
    List<Region> getRegionPath(Long regionId);
} 