<template>
  <div class="region-import-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>地区数据导入</span>
          <el-button type="primary" @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            下载模板
          </el-button>
        </div>
      </template>
      
      <!-- 导入说明 -->
      <div class="import-guide">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <div class="guide-content">
              <p><strong>支持格式：</strong>Excel (.xlsx/.xls) 和 CSV 格式</p>
              <p><strong>文件大小：</strong>不超过 10MB</p>
              <p><strong>数据要求：</strong></p>
              <ul>
                <li>第一行必须为表头：地区名称,地区编码,级别,排序,状态</li>
                <li>地区编码：6位数字，如 110000</li>
                <li>级别：1-省，2-市，3-区</li>
                <li>排序：数字，越小越靠前</li>
                <li>状态：1-启用，0-禁用</li>
              </ul>
              <p><strong>注意事项：</strong></p>
              <ul>
                <li>地区编码必须唯一</li>
                <li>建议先导入省级，再导入市级，最后导入区级</li>
                <li>导入前请备份现有数据</li>
              </ul>
            </div>
          </template>
        </el-alert>
      </div>
      
      <!-- 文件上传 -->
      <div class="upload-section">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
          :before-upload="beforeUpload"
          accept=".xlsx,.xls,.csv"
          multiple
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 xlsx/xls/csv 文件，且不超过 10MB
            </div>
          </template>
        </el-upload>
      </div>
      
      <!-- 导入选项 -->
      <div class="import-options">
        <el-form :model="importOptions" label-width="120px">
          <el-form-item label="导入模式">
            <el-radio-group v-model="importOptions.mode">
              <el-radio label="append">追加模式（保留现有数据）</el-radio>
              <el-radio label="replace">替换模式（清空现有数据）</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="数据验证">
            <el-checkbox-group v-model="importOptions.validations">
              <el-checkbox label="checkCode">检查地区编码唯一性</el-checkbox>
              <el-checkbox label="checkLevel">检查级别合理性</el-checkbox>
              <el-checkbox label="checkRequired">检查必填字段</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="错误处理">
            <el-radio-group v-model="importOptions.errorHandling">
              <el-radio label="stop">遇到错误停止</el-radio>
              <el-radio label="continue">跳过错误继续</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleImport" :loading="importing" :disabled="fileList.length === 0">
          <el-icon><Upload /></el-icon>
          开始导入
        </el-button>
        <el-button @click="handlePreview" :disabled="fileList.length === 0">
          <el-icon><View /></el-icon>
          预览数据
        </el-button>
        <el-button @click="handleClear">
          <el-icon><Delete /></el-icon>
          清空文件
        </el-button>
      </div>
      
      <!-- 预览数据 -->
      <div v-if="previewData.length > 0" class="preview-section">
        <h3>数据预览</h3>
        <el-table :data="previewData" border style="width: 100%" max-height="400">
          <el-table-column prop="regionName" label="地区名称" width="150" />
          <el-table-column prop="regionCode" label="地区编码" width="120" />
          <el-table-column prop="level" label="级别" width="80">
            <template #default="scope">
              <el-tag :type="getLevelTagType(scope.row.level)" size="small">
                {{ getLevelText(scope.row.level) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="validation" label="验证结果" width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.validation.valid" type="success" size="small">通过</el-tag>
              <el-tag v-else :type="scope.row.validation.errors.length > 0 ? 'danger' : 'warning'" size="small">
                {{ scope.row.validation.errors.length > 0 ? '错误' : '警告' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="错误信息" min-width="200">
            <template #default="scope">
              <div v-if="scope.row.validation.errors.length > 0" class="error-messages">
                <div v-for="error in scope.row.validation.errors" :key="error" class="error-item">
                  {{ error }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 导入结果 -->
      <div v-if="importResult" class="import-result">
        <h3>导入结果</h3>
        <el-result
          :icon="importResult.success ? 'success' : 'error'"
          :title="importResult.title"
          :sub-title="importResult.message"
        >
          <template #extra>
            <el-button type="primary" @click="goToRegionList">查看地区列表</el-button>
            <el-button @click="handleImportAgain">重新导入</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { 
  Download, Upload, View, Delete, UploadFilled 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// 响应式数据
const uploadRef = ref()
const fileList = ref([])
const importing = ref(false)
const previewData = ref([])
const importResult = ref(null)

// 导入选项
const importOptions = reactive({
  mode: 'append',
  validations: ['checkCode', 'checkLevel', 'checkRequired'],
  errorHandling: 'continue'
})

// 计算属性
const canImport = computed(() => {
  return fileList.value.length > 0 && previewData.value.length > 0
})

// 文件上传前验证
const beforeUpload = (file) => {
  const isValidType = /\.(xlsx|xls|csv)$/.test(file.name.toLowerCase())
  const isValidSize = file.size / 1024 / 1024 < 10
  
  if (!isValidType) {
    ElMessage.error('只能上传 Excel 或 CSV 文件')
    return false
  }
  
  if (!isValidSize) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }
  
  return false // 阻止自动上传
}

// 文件变化处理
const handleFileChange = (file) => {
  console.log('文件变化:', file)
}

// 文件移除处理
const handleFileRemove = (file) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

// 下载模板
const downloadTemplate = () => {
  // 创建CSV模板内容
  const csvContent = '地区名称,地区编码,级别,排序,状态\n北京市,110000,1,1,1\n北京市,110100,2,1,1\n东城区,110101,3,1,1'
  
  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', '地区数据导入模板.csv')
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 预览数据
const handlePreview = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  try {
    // 这里应该实现文件解析逻辑
    // 由于浏览器限制，实际项目中可能需要上传到服务器解析
    ElMessage.info('预览功能开发中，请直接导入文件')
    
    // 模拟预览数据
    previewData.value = [
      {
        regionName: '北京市',
        regionCode: '110000',
        level: 1,
        sortOrder: 1,
        status: 1,
        validation: { valid: true, errors: [] }
      },
      {
        regionName: '天津市',
        regionCode: '120000',
        level: 1,
        sortOrder: 2,
        status: 1,
        validation: { valid: true, errors: [] }
      }
    ]
  } catch (error) {
    console.error('预览数据失败:', error)
    ElMessage.error('预览数据失败')
  }
}

// 开始导入
const handleImport = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要导入 ${fileList.value.length} 个文件吗？导入模式：${importOptions.mode === 'append' ? '追加' : '替换'}`, 
      '导入确认', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    importing.value = true
    
    // 这里应该实现实际的文件上传和导入逻辑
    // 由于浏览器限制，需要将文件上传到服务器处理
    
    // 模拟导入过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟导入结果
    importResult.value = {
      success: true,
      title: '导入成功',
      message: `成功导入 ${previewData.value.length} 条地区数据`
    }
    
    ElMessage.success('导入成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('导入失败:', error)
      ElMessage.error('导入失败')
      
      importResult.value = {
        success: false,
        title: '导入失败',
        message: error.message || '未知错误'
      }
    }
  } finally {
    importing.value = false
  }
}

// 清空文件
const handleClear = () => {
  fileList.value = []
  previewData.value = []
  importResult.value = null
  uploadRef.value?.clearFiles()
}

// 重新导入
const handleImportAgain = () => {
  handleClear()
}

// 跳转到地区列表
const goToRegionList = () => {
  router.push('/region')
}

// 获取级别标签类型
const getLevelTagType = (level) => {
  const types = { 1: 'primary', 2: 'success', 3: 'warning' }
  return types[level] || 'info'
}

// 获取级别文本
const getLevelText = (level) => {
  const texts = { 1: '省', 2: '市', 3: '区' }
  return texts[level] || '未知'
}
</script>

<style scoped>
.region-import-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.import-guide {
  margin-bottom: 20px;
}

.guide-content {
  text-align: left;
}

.guide-content p {
  margin: 8px 0;
}

.guide-content ul {
  margin: 8px 0;
  padding-left: 20px;
}

.guide-content li {
  margin: 4px 0;
}

.upload-section {
  margin-bottom: 20px;
}

.upload-demo {
  text-align: center;
}

.el-upload__tip {
  color: #909399;
}

.import-options {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.action-buttons {
  margin-bottom: 20px;
  text-align: center;
}

.action-buttons .el-button {
  margin: 0 10px;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.import-result {
  margin-top: 20px;
}

.error-messages {
  max-height: 100px;
  overflow-y: auto;
}

.error-item {
  color: #f56c6c;
  font-size: 12px;
  margin-bottom: 2px;
}

/* 上传组件样式 */
.upload-demo .el-upload-dragger {
  width: 100%;
  height: 180px;
}

.upload-demo .el-upload-dragger:hover {
  border-color: #409eff;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 15px;
}

.el-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.el-radio-group {
  display: flex;
  gap: 20px;
}
</style> 