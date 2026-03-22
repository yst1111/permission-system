# 地区管理功能使用说明

## 概述

本系统提供了完整的地区管理功能，支持省市区的三级树形结构管理，包括地区列表展示、新增编辑、删除、数据导入等功能。

## 功能特性

### 1. 树形结构展示
- 支持省市区三级层级展示
- 可展开/收起所有节点
- 不同级别使用不同图标和颜色区分
- 支持按级别筛选

### 2. 地区管理操作
- 新增地区（支持指定上级地区）
- 编辑地区信息
- 删除地区（自动检查是否有子地区）
- 批量导入地区数据

### 3. 数据验证
- 地区编码唯一性检查
- 地区级别合理性验证
- 必填字段验证
- 导入数据预览和验证

## 页面说明

### 地区管理主页面 (`/region`)
- **功能**：地区列表展示、增删改查操作
- **特点**：树形表格展示，支持展开收起
- **操作**：新增、编辑、删除、刷新

### 地区数据导入页面 (`/region/import`)
- **功能**：批量导入地区数据
- **支持格式**：Excel (.xlsx/.xls)、CSV
- **特性**：数据预览、验证、导入选项配置

## 组件使用

### 1. RegionSelector 地区选择器

#### 基本用法
```vue
<template>
  <RegionSelector v-model="selectedRegion" />
</template>

<script setup>
import RegionSelector from '@/components/RegionSelector.vue'
import { ref } from 'vue'

const selectedRegion = ref(null)
</script>
```

#### 不同模式
```vue
<!-- 级联选择器（默认） -->
<RegionSelector 
  v-model="selectedRegion" 
  type="cascader" 
  placeholder="请选择地区"
/>

<!-- 树形选择器 -->
<RegionSelector 
  v-model="selectedRegion" 
  type="tree" 
  placeholder="请选择地区"
/>

<!-- 下拉选择器 -->
<RegionSelector 
  v-model="selectedRegion" 
  type="select" 
  placeholder="请选择地区"
/>
```

#### 高级配置
```vue
<RegionSelector 
  v-model="selectedRegion"
  type="cascader"
  :level="2"
  :parent-id="110000"
  :multiple="false"
  :filterable="true"
  @change="handleRegionChange"
/>
```

### 2. RegionDisplay 地区展示器

#### 基本用法
```vue
<template>
  <RegionDisplay 
    :region-id="regionId" 
    mode="path" 
    :show-detail="true"
  />
</template>

<script setup>
import RegionDisplay from '@/components/RegionDisplay.vue'
import { ref } from 'vue'

const regionId = ref(110101)
</script>
```

#### 不同显示模式
```vue
<!-- 路径模式 -->
<RegionDisplay 
  :region-id="regionId" 
  mode="path" 
  separator=" > "
/>

<!-- 标签模式 -->
<RegionDisplay 
  :region-id="regionId" 
  mode="tags"
/>

<!-- 文本模式 -->
<RegionDisplay 
  :region-id="regionId" 
  mode="text" 
  separator=" / "
/>
```

## API 接口说明

### 1. 地区列表
```
GET /api/region/list
参数：
- regionName: 地区名称（模糊查询）
- regionCode: 地区编码（模糊查询）
- level: 地区级别（1-省，2-市，3-区）
- status: 状态（0-禁用，1-启用）
- parentId: 父级地区ID
```

### 2. 地区树形结构
```
GET /api/region/tree
返回完整的地区树形结构
```

### 3. 新增地区
```
POST /api/region
请求体：
{
  "parentId": 0,
  "regionName": "北京市",
  "regionCode": "110000",
  "level": 1,
  "sortOrder": 1,
  "status": 1
}
```

### 4. 修改地区
```
PUT /api/region
请求体：同新增，需要包含id字段
```

### 5. 删除地区
```
DELETE /api/region/{id}
删除前会自动检查是否有子地区
```

### 6. 批量导入
```
POST /api/region/import
请求体：地区数据数组
```

## 数据格式

### 地区实体结构
```json
{
  "id": 1,
  "parentId": 0,
  "regionName": "北京市",
  "regionCode": "110000",
  "level": 1,
  "sortOrder": 1,
  "status": 1,
  "createTime": "2024-01-01T00:00:00",
  "updateTime": "2024-01-01T00:00:00",
  "children": []
}
```

### 导入数据格式
CSV/Excel 文件应包含以下列：
- 地区名称
- 地区编码
- 级别
- 排序
- 状态

示例数据：
```csv
地区名称,地区编码,级别,排序,状态
北京市,110000,1,1,1
北京市,110100,2,1,1
东城区,110101,3,1,1
```

## 使用建议

### 1. 数据导入
- 建议先导入省级数据，再导入市级，最后导入区级
- 导入前请备份现有数据
- 使用提供的模板文件确保数据格式正确

### 2. 地区编码
- 地区编码必须为1-20位数字
- 建议遵循国家标准编码规范
- 确保编码的唯一性
- 支持传统6位编码和扩展编码格式

### 3. 层级管理
- 省级地区 parentId 设为 0
- 市级地区 parentId 设为对应省级地区ID
- 区级地区 parentId 设为对应市级地区ID

### 4. 性能优化
- 大量数据时建议分批导入
- 定期清理无效数据
- 合理设置排序值

## 常见问题

### Q: 为什么无法删除地区？
A: 系统会检查是否有子地区，如果有子地区则无法删除。请先删除所有子地区。

### Q: 导入数据失败怎么办？
A: 请检查数据格式是否正确，地区编码是否重复，必填字段是否完整。

### Q: 如何修改地区层级关系？
A: 可以通过编辑地区信息，修改 parentId 字段来调整层级关系。

### Q: 支持哪些文件格式？
A: 支持 Excel (.xlsx/.xls) 和 CSV 格式，文件大小不超过 10MB。

## 扩展功能

### 1. 自定义验证规则
可以在服务层添加自定义的数据验证逻辑。

### 2. 地区数据导出
可以扩展导出功能，支持导出为 Excel 或 CSV 格式。

### 3. 地区统计
可以添加地区数据统计功能，如各地区数量统计等。

### 4. 地区搜索
可以增强搜索功能，支持拼音搜索、模糊匹配等。

## 技术支持

如有问题，请联系开发团队或查看系统日志获取详细错误信息。 