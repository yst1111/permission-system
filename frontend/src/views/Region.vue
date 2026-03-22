<template>
  <div class="region-container">
    <el-card>
      <!-- 筛选区域 -->
      <div class="filter-area">
        <el-form :model="queryForm" :inline="true" class="filter-form">
          <el-form-item label="地区名称">
            <el-input 
              v-model="queryForm.regionName" 
              placeholder="请输入地区名称" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="地区编码">
            <el-input 
              v-model="queryForm.regionCode" 
              placeholder="请输入地区编码" 
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="地区级别">
            <el-select v-model="queryForm.level" placeholder="请选择级别" clearable style="width: 120px">
              <el-option label="省" :value="1" />
              <el-option label="市" :value="2" />
              <el-option label="区" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div class="operation-area">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增地区
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="warning" @click="handleDownloadTemplate">
          <el-icon><Download /></el-icon>
          下载模板
        </el-button>
        <el-button type="info" @click="handleImport">
          <el-icon><Upload /></el-icon>
          导入数据
        </el-button>
      </div>
      
      <!-- 地区表格 - 使用折叠展示 -->
      <el-table
        v-loading="loading"
        :data="regionList"
        row-key="regionCode"
        border
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
        style="width: 100%"
        @row-click="handleRowClick"
        @expand-change="handleExpand"
      >
        <el-table-column prop="regionName" label="地区名称" width="250" min-width="200">
          <template #default="scope">
            <span class="region-name">
              <!-- 地区图标 -->
              <el-icon v-if="scope.row.level === 1" class="level-icon province"><Location /></el-icon>
              <el-icon v-else-if="scope.row.level === 2" class="level-icon city"><House /></el-icon>
              <el-icon v-else class="level-icon district"><OfficeBuilding /></el-icon>
              
              <!-- 地区名称 -->
              {{ scope.row.regionName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="展开" width="80" align="center">
          <template #default="scope">
            <el-button 
              v-if="scope.row.hasChildren"
              size="small" 
              type="text"
              @click.stop="handleManualExpand(scope.row)"
            >
              <el-icon><Expand /></el-icon>
              展开
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="regionCode" label="地区编码" width="150" />
        <el-table-column prop="level" label="级别" width="100">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)" size="small">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hasChildren" label="有子节点" width="100">
          <template #default="scope">
            <span>{{ scope.row.hasChildren ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleAdd(scope.row)">
              <el-icon><Plus /></el-icon>
              新增子级
            </el-button>
            <el-button size="small" type="warning" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 地区表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form ref="regionFormRef" :model="regionForm" :rules="regionRules" label-width="100px">
        <el-form-item label="上级地区">
          <el-tree-select
            v-model="regionForm.parentCode"
            :data="regionTreeData"
            :props="{ label: 'regionName', value: 'regionCode' }"
            placeholder="请选择上级地区"
            clearable
            check-strictly
            style="width: 100%"
            @change="handleParentChange"
          />
        </el-form-item>
        <el-form-item label="地区名称" prop="regionName">
          <el-input v-model="regionForm.regionName" placeholder="请输入地区名称" />
        </el-form-item>
        <el-form-item label="地区编码" prop="regionCode">
          <el-input v-model="regionForm.regionCode" placeholder="请输入地区编码" />
          <div class="form-tip">地区编码为1-20位数字，如：110000、12345678901234567890</div>
        </el-form-item>
        <el-form-item label="地区级别" prop="level">
          <el-select v-model="regionForm.level" placeholder="请选择地区级别" style="width: 100%">
            <el-option label="省" :value="1" />
            <el-option label="市" :value="2" />
            <el-option label="区" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="regionForm.sortOrder" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="regionForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 导入数据对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入地区数据"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
        accept=".xlsx,.xls"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>
      <div class="import-tips">
        <h4>导入说明：</h4>
        <p>1. 支持 Excel (.xlsx/.xls) 格式</p>
        <p>2. 请使用"下载模板"按钮下载标准模板</p>
        <p>3. 必填字段：地区编码*、地区名称*、地区级别*</p>
        <p>4. 地区编码：1-20位数字，如：110000、12345678901234567890</p>
        <p>5. 可选字段：父级编码、排序、状态</p>
        <p>6. 地区级别：1-省，2-市，3-区</p>
        <p>7. 状态：0-禁用，1-启用</p>
        <p>8. 父级编码为空表示省级地区</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImportSubmit" :loading="submitLoading">开始导入</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Refresh, Plus, Edit, Delete, Expand, Upload, Download,
  Location, House, OfficeBuilding, UploadFilled
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 响应式数据
const regionList = ref([])
const regionTreeData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const regionFormRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const importDialogVisible = ref(false)
const fileList = ref([])


// 查询表单
const queryForm = reactive({
  regionName: '',
  regionCode: '',
  level: null,
  status: null
})

const regionForm = reactive({
  id: null,
  parentCode: '',
  regionName: '',
  regionCode: '',
  level: 1,
  sortOrder: 0,
  status: 1
})

const regionRules = {
  regionName: [
    { required: true, message: '请输入地区名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  regionCode: [
    { required: true, message: '请输入地区编码', trigger: 'blur' },
    { pattern: /^[0-9]{1,20}$/, message: '地区编码必须为1-20位数字', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择地区级别', trigger: 'change' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}



// 为地区数据添加 hasChildren 属性
const addHasChildrenProperty = (regions) => {
  regions.forEach(region => {
    // 只有省级和市级可能有子节点
    if (region.level < 3) {
      // 强制设置为true，让展开按钮显示
      region.hasChildren = true
      // 确保children数组存在
      if (!region.children) {
        region.children = []
      }
    } else {
      // 区级没有子节点
      region.hasChildren = false
      region.children = []
    }
  })
  return regions
}

// 查询地区列表
const fetchRegionList = async () => {
  loading.value = true
  try {
    // 如果没有查询条件，默认只查询省级地区
    if (!queryForm.regionName && !queryForm.regionCode && !queryForm.level && !queryForm.status) {
      const response = await request.get('/api/region/provinces')
      regionList.value = addHasChildrenProperty(response.data.data || [])
    } else {
      const response = await request.get('/api/region/list', { params: queryForm })
      regionList.value = addHasChildrenProperty(response.data.data || [])
    }
    

  } catch (error) {
    console.error('查询地区列表失败:', error)
    ElMessage.error('查询地区列表失败')
  } finally {
    loading.value = false
  }
}

// 查询地区树形结构
const fetchRegionTree = async () => {
  try {
    const response = await request.get('/api/region/tree')
    const treeData = response.data.data || []
    
    // 为树形数据添加 hasChildren 属性
    const addTreeHasChildren = (regions) => {
      regions.forEach(region => {
        region.hasChildren = region.children && region.children.length > 0
        if (region.children && region.children.length > 0) {
          addTreeHasChildren(region.children)
        }
      })
    }
    addTreeHasChildren(treeData)
    
    regionTreeData.value = [{ id: 0, regionName: '顶级地区' }, ...treeData]
    regionList.value = addHasChildrenProperty(treeData)
  } catch (error) {
    console.error('查询地区树失败:', error)
    ElMessage.error('查询地区树失败')
  }
}

// 方法
const getLevelTagType = (level) => {
  const types = { 1: 'primary', 2: 'success', 3: 'warning' }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = { 1: '省', 2: '市', 3: '区' }
  return texts[level] || '未知'
}

const handleAdd = (row) => {
  dialogTitle.value = '新增地区'
  dialogVisible.value = true
  resetForm()
  if (row) {
    regionForm.parentCode = row.regionCode
    regionForm.level = Math.min(row.level + 1, 3)
  }
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑地区'
  dialogVisible.value = true
  Object.assign(regionForm, row)
}

const handleRowClick = (row) => {
  // 可以在这里添加行点击事件处理
  console.log('点击行:', row)
}

const handleParentChange = (parentCode) => {
  if (parentCode === '') {
    regionForm.level = 1
  } else {
    const parent = regionTreeData.value.find(item => item.regionCode === parentCode)
    if (parent) {
      regionForm.level = Math.min(parent.level + 1, 3)
    }
  }
}

// 查询按钮
const handleQuery = () => {
  fetchRegionList()
}

// 重置按钮
const handleReset = () => {
  Object.assign(queryForm, {
    regionName: '',
    regionCode: '',
    level: null,
    status: null
  })
  fetchRegionList()
}

// 刷新按钮
const handleRefresh = () => {
  fetchRegionList()
  fetchRegionTree() // 始终更新树形数据用于表单选择
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除地区"${row.regionName}"吗？${row.hasChildren ? '删除后其下所有子地区也将被删除！' : ''}`, 
      '删除确认', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    submitLoading.value = true
    await request.delete(`/api/region/${row.regionCode}`)
    
    ElMessage.success('删除成功')
    fetchRegionList()
    fetchRegionTree() // 始终更新树形数据用于表单选择
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地区失败:', error)
      ElMessage.error('删除地区失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 使用Element Plus表格的自动展开/收起功能
// 当表格展开节点时，自动加载子节点数据

// 处理表格展开事件
const handleExpand = async (row, expanded) => {
  if (expanded && (!row.children || row.children.length === 0)) {
    try {
      const response = await request.get(`/api/region/children/${row.regionCode}`)
      const children = response.data.data || []
      
      // 为每个子节点添加 hasChildren 属性
      children.forEach(child => {
        child.hasChildren = child.level < 3 // 区级没有子节点
      })
      
      // 更新当前行的子节点
      row.children = children
    } catch (error) {
      console.error('加载子节点失败:', error)
      ElMessage.error('加载子节点失败')
    }
  }
}

// 手动展开处理函数
const handleManualExpand = async (row) => {
  try {
    // 如果已经有子节点数据，直接展开
    if (row.children && row.children.length > 0) {
      // 这里可以添加展开状态的切换逻辑
      console.log('展开已有子节点:', row)
      return
    }
    
    // 加载子节点数据
    const response = await request.get(`/api/region/children/${row.regionCode}`)
    const children = response.data.data || []
    
    // 为每个子节点添加 hasChildren 属性
    children.forEach(child => {
      child.hasChildren = child.level < 3
    })
    
    // 更新当前行的子节点
    row.children = children
    
    // 显示成功消息
    ElMessage.success(`已加载 ${children.length} 个子节点`)
    
  } catch (error) {
    console.error('加载子节点失败:', error)
    ElMessage.error('加载子节点失败')
  }
}

// 下载Excel模板
const handleDownloadTemplate = async () => {
  try {
    const response = await request.get('/api/region/template/download', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '地区导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('模板下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败，请重试')
  }
}

const handleImport = () => {
  importDialogVisible.value = true
  fileList.value = []
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleImportSubmit = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  const file = fileList.value[0]
  
  // 检查文件格式
  if (!file.name.toLowerCase().endsWith('.xlsx') && !file.name.toLowerCase().endsWith('.xls')) {
    ElMessage.error('请选择Excel文件（.xlsx或.xls格式）')
    return
  }
  
  try {
    submitLoading.value = true
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file.raw || file)
    
    // 调用导入接口
    const response = await request.post('/api/region/import/file', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('导入成功')
      // 显示导入结果
      ElMessageBox.alert(response.data.data, '导入结果', {
        confirmButtonText: '确定',
        type: 'info'
      })
      
      // 刷新数据
      fetchRegionList()
      fetchRegionTree() // 始终更新树形数据用于表单选择
      
      // 关闭导入对话框
      importDialogVisible.value = false
      fileList.value = []
    } else {
      ElMessage.error(response.data.message || '导入失败')
    }
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!regionFormRef.value) return
  
  try {
    await regionFormRef.value.validate()
    
    submitLoading.value = true
    
    if (regionForm.id) {
      await request.put('/api/region', regionForm)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/region', regionForm)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    resetForm()
    fetchRegionList()
    fetchRegionTree() // 始终更新树形数据用于表单选择
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  Object.assign(regionForm, {
    id: null,
    parentCode: '',
    regionName: '',
    regionCode: '',
    level: 1,
    sortOrder: 0,
    status: 1
  })
  if (regionFormRef.value) {
    regionFormRef.value.resetFields()
  }
}

// 生命周期
onMounted(() => {
  fetchRegionList() // 默认只加载省级地区
  fetchRegionTree() // 加载完整树形数据用于表单选择
})
</script>

<style scoped>
.region-container {
  padding: 20px;
}

.filter-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.filter-form {
  margin-bottom: 0;
}

.operation-area {
  margin-bottom: 20px;
}

.operation-area .el-button {
  margin-right: 10px;
}

.operation-area .el-button:last-child {
  margin-right: 0;
}

.dialog-footer {
  text-align: right;
}

.el-table {
  margin-top: 20px;
}

.region-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-icon {
  font-size: 16px;
}

.level-icon.province {
  color: #409eff;
}

.level-icon.city {
  color: #67c23a;
}

.level-icon.district {
  color: #e6a23c;
}



.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.import-tips {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.import-tips h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.import-tips p {
  margin: 5px 0;
  color: #606266;
  font-size: 13px;
}

.upload-demo {
  text-align: center;
}

.el-upload__tip {
  color: #909399;
}

/* 表格行样式 */
.el-table .el-table__row:hover {
  background-color: #f5f7fa;
}

/* 树形表格样式 */
.el-table .el-table__expand-icon {
  color: #409eff;
}

.el-table .el-table__expand-icon:hover {
  color: #66b1ff;
}
</style> 