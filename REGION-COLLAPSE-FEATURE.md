# 地区管理折叠功能实现说明

## 功能概述

实现了地区管理的折叠展示功能，默认只展示省级地区，用户可以通过点击展开按钮逐步查看市级和区级地区，提供更好的用户体验和性能优化。

## 功能特性

### 1. 默认折叠状态
- 页面加载时默认只显示省级地区
- 减少初始数据加载量，提升页面性能
- 清晰的层级结构展示

### 2. 渐进式展开
- 点击省级地区的展开按钮 → 显示该省下的市级地区
- 点击市级地区的展开按钮 → 显示该市下的区级地区
- 区级地区没有子节点，不显示展开按钮

### 3. 全局展开/收起
- "展开"按钮：展开所有节点，显示完整的地区树
- "收起"按钮：收起所有节点，只显示省级地区
- 智能状态管理，避免重复加载数据

### 4. 数据懒加载
- 只在需要时加载子节点数据
- 减少不必要的数据传输
- 提升系统响应速度

## 技术实现

### 后端修改

#### 1. 新增API接口

**获取省级地区列表**
```java
@GetMapping("/provinces")
public Result<List<Region>> getProvinces()
```
- 专门用于获取省级地区，支持折叠展示
- 返回所有 level=1 的地区

**根据级别查询地区**
```java
@GetMapping("/level/{level}")
public Result<List<Region>> getRegionsByLevel(@PathVariable Integer level)
```
- 支持按级别查询地区（1-省，2-市，3-区）
- 提供更灵活的查询方式

#### 2. 现有接口优化
- `/api/region/children/{parentId}` - 获取指定父级下的子地区
- `/api/region/tree` - 获取完整地区树形结构

### 前端修改

#### 1. 数据结构优化
```javascript
// 为地区数据添加 hasChildren 属性
const addHasChildrenProperty = (regions) => {
  regions.forEach(region => {
    region.hasChildren = region.level < 3 // 省市级有子节点，区级没有
  })
  return regions
}
```

#### 2. 折叠状态管理
```javascript
const isExpandAll = ref(false) // 默认收起
const expandedKeys = ref(new Set()) // 记录已展开的节点
```

#### 3. 智能数据加载
```javascript
// 处理展开/折叠变化
const handleExpandChange = async (row, expanded) => {
  if (expanded && row.children && row.children.length === 0) {
    // 展开时，如果还没有子节点数据，则加载子节点
    const response = await request.get(`/api/region/children/${row.id}`)
    const children = response.data.data || []
    
    // 为每个子节点添加 hasChildren 属性
    children.forEach(child => {
      child.hasChildren = child.level < 3
    })
    
    // 更新当前行的子节点
    row.children = children
    expandedKeys.value.add(row.id)
  }
}
```

#### 4. 条件查询优化
```javascript
// 查询地区列表
const fetchRegionList = async () => {
  // 如果没有查询条件，默认只查询省级地区
  if (!queryForm.regionName && !queryForm.regionCode && !queryForm.level && !queryForm.status) {
    const response = await request.get('/api/region/provinces')
    regionList.value = addHasChildrenProperty(response.data.data || [])
  } else {
    const response = await request.get('/api/region/list', { params: queryForm })
    regionList.value = addHasChildrenProperty(response.data.data || [])
  }
}
```

## 使用说明

### 1. 基本操作
1. **查看省级地区**：页面加载后默认显示所有省级地区
2. **展开市级地区**：点击省级地区前的展开按钮（▶）
3. **展开区级地区**：点击市级地区前的展开按钮（▶）
4. **收起节点**：点击展开状态下的收起按钮（▼）

### 2. 全局操作
1. **展开所有**：点击"展开"按钮，显示完整的地区树
2. **收起所有**：点击"收起"按钮，只显示省级地区
3. **刷新数据**：点击"刷新"按钮，根据当前状态重新加载数据

### 3. 查询功能
1. **条件查询**：使用筛选条件查询特定地区
2. **重置查询**：重置后回到默认的省级展示状态
3. **智能切换**：根据查询条件自动切换展示模式

## 性能优化

### 1. 数据加载优化
- 初始只加载省级数据，减少数据传输
- 按需加载子节点，避免一次性加载大量数据
- 智能缓存已展开的节点数据

### 2. 用户体验优化
- 清晰的层级结构展示
- 直观的展开/收起操作
- 响应式的数据加载反馈

### 3. 系统资源优化
- 减少不必要的数据库查询
- 优化前端渲染性能
- 合理的内存使用

## 扩展功能

### 1. 批量操作
- 支持批量展开/收起指定级别的地区
- 支持按区域批量操作

### 2. 搜索高亮
- 搜索结果高亮显示
- 自动展开包含搜索结果的节点

### 3. 状态持久化
- 记住用户的展开/收起状态
- 支持自定义默认展开级别

## 注意事项

1. **数据一致性**：确保地区数据的父子关系正确
2. **权限控制**：根据用户权限控制可查看的地区范围
3. **性能监控**：监控大量数据展开时的性能表现
4. **错误处理**：妥善处理数据加载失败的情况

## 总结

地区管理折叠功能的实现，既提升了系统性能，又改善了用户体验。通过智能的数据加载策略和直观的操作界面，用户可以高效地浏览和管理地区数据，同时系统资源得到合理利用。 