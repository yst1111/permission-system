package com.permission.controller;

import com.permission.common.Result;
import com.permission.common.ExcelUtils;
import com.permission.entity.Region;
import com.permission.service.RegionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 地区管理控制器
 * 
 * @author system
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/region")
@Api(tags = "地区管理", description = "地区相关接口")
public class RegionController {

    @Autowired
    private RegionService regionService;
    
    @Autowired
    private ExcelUtils excelUtils;

    /**
     * 获取地区列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取地区列表", notes = "查询地区列表，支持条件查询")
    public Result<List<Region>> getRegionList(@ApiParam(value = "查询条件") Region region) {
        try {
            List<Region> regions = regionService.getRegionList(region);
            return Result.success(regions);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取地区
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取地区", notes = "根据地区ID查询地区信息")
    public Result<Region> getRegionById(@ApiParam(value = "地区ID") @PathVariable Long id) {
        try {
            Region region = regionService.getRegionById(id);
            return region != null ? Result.success(region) : Result.error("地区不存在");
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取地区树形结构
     */
    @GetMapping("/tree")
    @ApiOperation(value = "获取地区树形结构", notes = "获取完整的地区树形结构")
    public Result<List<Region>> getRegionTree() {
        try {
            List<Region> regions = regionService.getRegionTree();
            return Result.success(regions);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据父级编码获取子地区
     */
    @GetMapping("/children/{parentCode}")
    @ApiOperation(value = "根据父级编码获取子地区", notes = "根据父级地区编码查询子地区列表")
    public Result<List<Region>> getChildrenByParentCode(@ApiParam(value = "父级地区编码") @PathVariable String parentCode) {
        try {
            List<Region> regions = regionService.getRegionList(new Region() {{
                setParentCode(parentCode);
            }});
            return Result.success(regions);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据级别获取地区列表
     */
    @GetMapping("/level/{level}")
    @ApiOperation(value = "根据级别获取地区列表", notes = "根据地区级别查询地区列表")
    public Result<List<Region>> getRegionsByLevel(@ApiParam(value = "地区级别：1-省，2-市，3-区") @PathVariable Integer level) {
        try {
            List<Region> regions = regionService.getRegionList(new Region() {{
                setLevel(level);
            }});
            return Result.success(regions);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取省级地区列表（用于折叠展示）
     */
    @GetMapping("/provinces")
    @ApiOperation(value = "获取省级地区列表", notes = "获取所有省级地区，用于折叠展示")
    public Result<List<Region>> getProvinces() {
        try {
            List<Region> provinces = regionService.getRegionList(new Region() {{
                setLevel(1);
            }});
            return Result.success(provinces);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取地区路径
     */
    @GetMapping("/path/{id}")
    @ApiOperation(value = "获取地区路径", notes = "获取从根到指定地区的完整路径")
    public Result<List<Region>> getRegionPath(@ApiParam(value = "地区ID") @PathVariable Long id) {
        try {
            List<Region> path = regionService.getRegionPath(id);
            return Result.success(path);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据父级编码获取子地区数量
     */
    @GetMapping("/count/children/{parentCode}")
    @ApiOperation(value = "根据父级编码获取子地区数量", notes = "根据父级地区编码查询子地区数量")
    public Result<Integer> getChildrenCount(@ApiParam(value = "父级地区编码") @PathVariable String parentCode) {
        try {
            int count = regionService.getChildrenCount(parentCode);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 新增地区
     */
    @PostMapping
    @ApiOperation(value = "新增地区", notes = "添加新地区")
    public Result<String> addRegion(@ApiParam(value = "地区信息") @RequestBody Region region) {
        try {
            if (regionService.addRegion(region)) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 修改地区
     */
    @PutMapping
    @ApiOperation(value = "修改地区", notes = "更新地区信息")
    public Result<String> updateRegion(@ApiParam(value = "地区信息") @RequestBody Region region) {
        try {
            if (regionService.updateRegion(region)) {
                return Result.success("修改成功");
            } else {
                return Result.error("修改失败");
            }
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    /**
     * 删除地区
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除地区", notes = "根据地区ID删除地区")
    public Result<String> deleteRegion(@ApiParam(value = "地区ID") @PathVariable Long id) {
        try {
            if (regionService.deleteRegion(id)) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量导入地区数据（文件上传）
     */
    @PostMapping("/import/file")
    @ApiOperation(value = "批量导入地区数据", notes = "通过Excel文件批量导入地区数据")
    public Result<String> importRegionsFromFile(@ApiParam(value = "Excel文件") @RequestParam("file") MultipartFile file) {
        try {
            // 检查文件
            if (file.isEmpty()) {
                return Result.error("请选择要导入的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                return Result.error("请选择Excel文件（.xlsx或.xls格式）");
            }
            
            // 解析Excel文件
            InputStream inputStream = file.getInputStream();
            List<Region> regions = excelUtils.parseRegionImportFile(inputStream, fileName);
            
            if (regions.isEmpty()) {
                return Result.error("Excel文件中没有有效数据，请检查文件内容");
            }
            
            // 处理父级编码，查找对应的父级ID
            processParentIds(regions);
            
            // 导入数据
            String result = regionService.importRegions(regions);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量导入地区数据（JSON数据）
     */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入地区数据", notes = "通过JSON数据批量导入地区数据")
    public Result<String> importRegions(@ApiParam(value = "地区数据列表") @RequestBody List<Region> regions) {
        try {
            String result = regionService.importRegions(regions);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 下载地区导入Excel模板
     */
    @GetMapping("/template/download")
    @ApiOperation(value = "下载地区导入Excel模板", notes = "下载地区批量导入的Excel模板文件")
    public ResponseEntity<byte[]> downloadRegionTemplate() {
        try {
            byte[] excelData = excelUtils.generateRegionImportTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "地区导入模板.xlsx");
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 处理父级编码，查找对应的父级ID
     */
    private void processParentIds(List<Region> regions) {
        for (Region region : regions) {
            if (region.getParentCode() == null || region.getParentCode().trim().isEmpty()) {
                // 如果父级编码为空，说明是省级地区
                region.setParentCode("");
            } else {
                // 验证父级编码是否存在
                try {
                    List<Region> parentRegions = regionService.getRegionList(new Region() {{
                        setRegionCode(region.getParentCode().trim());
                    }});
                    if (parentRegions == null || parentRegions.isEmpty()) {
                        // 如果找不到父级，记录错误信息
                        throw new RuntimeException("找不到父级编码为 " + region.getParentCode() + " 的地区，请确保父级地区已存在");
                    }
                    // 父级编码存在，保持原值
                } catch (Exception e) {
                    throw new RuntimeException("处理父级编码 " + region.getParentCode() + " 时出错：" + e.getMessage());
                }
            }
        }
    }
    

} 