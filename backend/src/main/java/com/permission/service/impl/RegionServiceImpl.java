package com.permission.service.impl;

import com.permission.entity.Region;
import com.permission.mapper.RegionMapper;
import com.permission.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地区服务实现类
 * 
 * @author system
 * @since 2024-01-01
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<Region> getRegionList(Region region) {
        return regionMapper.selectRegionList(region);
    }

    @Override
    public Region getRegionById(Long id) {
        return regionMapper.selectRegionById(id);
    }

    @Override
    public List<Region> getRegionTree() {
        List<Region> allRegions = regionMapper.selectRegionTree();
        return buildRegionTree(allRegions, null);
    }

    @Override
    public boolean addRegion(Region region) {
        // 检查地区编码是否已存在
        if (isRegionCodeExists(region.getRegionCode(), null)) {
            throw new RuntimeException("地区编码已存在");
        }
        
        // 设置创建时间和更新时间
        region.setCreateTime(LocalDateTime.now());
        region.setUpdateTime(LocalDateTime.now());
        
        // 如果没有设置排序，默认设为0
        if (region.getSortOrder() == null) {
            region.setSortOrder(0);
        }
        
        // 如果没有设置状态，默认启用
        if (region.getStatus() == null) {
            region.setStatus("1");
        }
        
        return regionMapper.insertRegion(region) > 0;
    }

    @Override
    public boolean updateRegion(Region region) {
        // 检查地区编码是否已存在（排除当前记录）
        if (isRegionCodeExists(region.getRegionCode(), region.getId())) {
            throw new RuntimeException("地区编码已存在");
        }
        
        // 设置更新时间
        region.setUpdateTime(LocalDateTime.now());
        
        return regionMapper.updateRegion(region) > 0;
    }

    @Override
    public boolean deleteRegion(Long id) {
        // 检查是否有子地区
        if (regionMapper.checkHasChildren(id) > 0) {
            throw new RuntimeException("该地区下还有子地区，不能删除");
        }
        
        return regionMapper.deleteRegionById(id) > 0;
    }

    @Override
    public boolean isRegionCodeExists(String regionCode, Long excludeId) {
        return regionMapper.checkRegionCodeExists(regionCode, excludeId) > 0;
    }

    @Override
    public String importRegions(List<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return "导入数据为空";
        }
        
        int successCount = 0;
        int failCount = 0;
        StringBuilder result = new StringBuilder();
        
        for (Region region : regions) {
            try {
                // 设置默认值
                if (region.getStatus() == null) {
                    region.setStatus("1");
                }
                if (region.getSortOrder() == null) {
                    region.setSortOrder(0);
                }
                if (region.getCreateTime() == null) {
                    region.setCreateTime(LocalDateTime.now());
                }
                if (region.getUpdateTime() == null) {
                    region.setUpdateTime(LocalDateTime.now());
                }
                
                // 检查地区编码是否已存在
                if (isRegionCodeExists(region.getRegionCode(), null)) {
                    result.append("地区编码 ").append(region.getRegionCode()).append(" 已存在，跳过导入\n");
                    failCount++;
                    continue;
                }
                
                // 插入地区
                if (regionMapper.insertRegion(region) > 0) {
                    successCount++;
                } else {
                    failCount++;
                    result.append("地区 ").append(region.getRegionName()).append(" 导入失败\n");
                }
            } catch (Exception e) {
                failCount++;
                result.append("地区 ").append(region.getRegionName()).append(" 导入异常：").append(e.getMessage()).append("\n");
            }
        }
        
        result.insert(0, String.format("导入完成：成功 %d 条，失败 %d 条\n", successCount, failCount));
        return result.toString();
    }

    @Override
    public int getChildrenCount(String parentCode) {
        if (parentCode == null || parentCode.trim().isEmpty()) {
            return 0;
        }
        // 根据父级编码查找子地区数量
        List<Region> children = getRegionList(new Region() {{
            setParentCode(parentCode);
        }});
        return children != null ? children.size() : 0;
    }

    @Override
    public List<Region> getRegionPath(Long regionId) {
        List<Region> path = new java.util.ArrayList<>();
        Region current = getRegionById(regionId);
        
        while (current != null) {
            path.add(0, current);
            if (current.getParentCode() != null && !current.getParentCode().trim().isEmpty()) {
                // 根据父级编码查找父级地区
                final String parentCode = current.getParentCode();
                List<Region> parentRegions = getRegionList(new Region() {{
                    setRegionCode(parentCode);
                }});
                if (parentRegions != null && !parentRegions.isEmpty()) {
                    current = parentRegions.get(0);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        return path;
    }

    /**
     * 构建地区树形结构
     * 
     * @param allRegions 所有地区
     * @param parentCode 父级编码
     * @return 树形结构
     */
    private List<Region> buildRegionTree(List<Region> allRegions, String parentCode) {
        return allRegions.stream()
                .filter(region -> {
                    if (parentCode == null || parentCode.trim().isEmpty()) {
                        return region.getParentCode() == null || region.getParentCode().trim().isEmpty();
                    } else {
                        return parentCode.equals(region.getParentCode());
                    }
                })
                .filter(region -> "1".equals(region.getStatus())) // 只显示启用的地区
                .map(region -> {
                    region.setChildren(buildRegionTree(allRegions, region.getRegionCode()));
                    return region;
                })
                .sorted((r1, r2) -> {
                    if (r1.getSortOrder() == null) r1.setSortOrder(0);
                    if (r2.getSortOrder() == null) r2.setSortOrder(0);
                    return r1.getSortOrder().compareTo(r2.getSortOrder());
                })
                .collect(Collectors.toList());
    }
} 