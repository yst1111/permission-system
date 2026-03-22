# 父级编码迁移指南

## 概述

本指南详细说明了如何将地区管理模块从使用 `parent_id`（父级ID）改为使用 `parent_code`（父级编码）的完整迁移过程。

## 迁移原因

### 1. 原有问题
- **数据关联复杂**：通过ID关联需要额外的查询操作
- **导入困难**：Excel导入时需要先查找父级ID
- **维护复杂**：ID变化会影响所有子地区的关系
- **可读性差**：无法直观看出地区间的层级关系

### 2. 迁移优势
- **直观明了**：通过编码直接看出地区层级关系
- **导入简单**：Excel中直接填写父级编码即可
- **维护方便**：编码变化不影响数据库ID
- **数据一致**：编码具有业务含义，更稳定

## 迁移步骤

### 第一步：数据库迁移

#### 1.1 执行迁移脚本
```sql
-- 执行 database-migration-script.sql 中的脚本
-- 注意：执行前请先备份数据库
```

#### 1.2 验证数据完整性
```sql
-- 检查是否有孤立的子地区
SELECT r1.id, r1.region_name, r1.region_code, r1.parent_code
FROM sys_region r1
WHERE r1.parent_code != '' 
AND r1.parent_code NOT IN (
    SELECT r2.region_code 
    FROM sys_region r2
);
```

#### 1.3 回滚准备（可选）
如果迁移出现问题，可以使用回滚脚本恢复原有结构。

### 第二步：后端代码修改

#### 2.1 实体类修改
- `Region.java`：将 `parentId` 字段改为 `parentCode` 字段
- 更新相关的 getter、setter 和构造方法

#### 2.2 Mapper接口修改
- `RegionMapper.java`：将 `selectRegionByParentId` 改为 `selectRegionByParentCode`
- 更新方法参数类型从 `Long` 改为 `String`

#### 2.3 MyBatis映射文件修改
- `RegionMapper.xml`：将所有 `parent_id` 字段改为 `parent_code`
- 更新SQL语句中的字段引用

#### 2.4 服务层修改
- `RegionService.java`：更新接口方法签名
- `RegionServiceImpl.java`：更新实现逻辑，处理编码关联

#### 2.5 控制器修改
- `RegionController.java`：更新API接口，使用编码而不是ID
- 修改导入逻辑，直接使用父级编码

#### 2.6 工具类修改
- `ExcelUtils.java`：更新Excel解析逻辑，直接设置父级编码

### 第三步：前端代码修改

#### 3.1 地区管理页面
- `Region.vue`：将表单中的 `parentId` 改为 `parentCode`
- 更新树形选择器的值绑定
- 修改相关的处理函数

#### 3.2 地区选择器组件
- `RegionSelector.vue`：更新属性定义和API调用
- 修改父级地区的获取逻辑

## 代码变更详情

### 后端变更

#### Region实体类
```java
// 修改前
private Long parentId;

// 修改后
private String parentCode;
```

#### RegionMapper接口
```java
// 修改前
List<Region> selectRegionByParentId(@Param("parentId") Long parentId);

// 修改后
List<Region> selectRegionByParentCode(@Param("parentCode") String parentCode);
```

#### MyBatis映射
```xml
<!-- 修改前 -->
<result column="parent_id" property="parentId" jdbcType="BIGINT"/>

<!-- 修改后 -->
<result column="parent_code" property="parentCode" jdbcType="VARCHAR"/>
```

### 前端变更

#### 地区表单
```vue
<!-- 修改前 -->
<el-tree-select v-model="regionForm.parentId" :props="{ value: 'id' }" />

<!-- 修改后 -->
<el-tree-select v-model="regionForm.parentCode" :props="{ value: 'regionCode' }" />
```

#### 数据结构
```javascript
// 修改前
const regionForm = {
  parentId: 0,
  // ...其他字段
}

// 修改后
const regionForm = {
  parentCode: '',
  // ...其他字段
}
```

## 数据迁移示例

### 迁移前数据结构
```sql
-- 原有数据
INSERT INTO sys_region (parent_id, region_name, region_code, level) VALUES 
(0, '北京市', '110000', 1),
(1, '北京市', '110100', 2),
(2, '东城区', '110101', 3);
```

### 迁移后数据结构
```sql
-- 迁移后数据
INSERT INTO sys_region (parent_code, region_name, region_code, level) VALUES 
('', '北京市', '110000', 1),
('110000', '北京市', '110100', 2),
('110100', '东城区', '110101', 3);
```

## 测试验证

### 1. 功能测试
- [ ] 新增地区时上级地区选择正常
- [ ] 编辑地区时上级地区显示正确
- [ ] 地区列表层级关系正确
- [ ] 导入Excel时父级编码处理正确

### 2. 数据测试
- [ ] 数据库字段结构正确
- [ ] 现有数据迁移完整
- [ ] 新数据插入正常
- [ ] 查询功能正常

### 3. 性能测试
- [ ] 地区树加载速度
- [ ] 查询响应时间
- [ ] 导入处理效率

## 注意事项

### 1. 数据备份
- 迁移前必须备份完整数据库
- 建议在测试环境先验证迁移脚本

### 2. 兼容性
- 确保所有相关代码都已更新
- 检查是否有遗漏的字段引用

### 3. 性能影响
- 编码查询可能需要额外的索引
- 大量数据时注意查询性能

### 4. 错误处理
- 迁移过程中可能出现数据不一致
- 需要准备回滚方案

## 常见问题

### Q: 迁移后数据丢失怎么办？
A: 使用回滚脚本恢复原有结构，然后重新执行迁移。

### Q: 前端显示异常怎么办？
A: 检查浏览器控制台错误，确认所有字段都已正确更新。

### Q: 导入功能不工作怎么办？
A: 检查Excel解析逻辑，确认父级编码字段处理正确。

### Q: 性能下降怎么办？
A: 检查数据库索引，确保 `parent_code` 和 `region_code` 字段有适当的索引。

## 总结

通过这次迁移，地区管理模块将具有以下优势：

1. **更直观的数据结构**：通过编码直接看出地区关系
2. **更简单的导入流程**：Excel中直接填写父级编码
3. **更稳定的数据关联**：编码变化不影响数据库关系
4. **更好的维护性**：减少复杂的ID查询逻辑

迁移完成后，建议进行全面的功能测试，确保所有功能正常工作。 