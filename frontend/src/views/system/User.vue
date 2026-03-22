<template>
  <div class="user-container">
    <el-card>
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="searchForm.gender" placeholder="请选择性别" clearable>
              <el-option label="未知" :value="0" />
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮 -->
      <div class="operation-area">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
        <el-button type="success" @click="handleImport">导入用户</el-button>
        <el-button type="warning" @click="handleExport">导出用户</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedUsers.length === 0">
          批量删除 ({{ selectedUsers.length }})
        </el-button>
      </div>
      
      <!-- 表格 -->
      <el-table 
        :data="userList" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="scope.row.gender === 2" type="danger">女</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="scope.row.username === 'admin'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="scope">
            <span v-if="scope.row.lastLoginTime">{{ formatDateTime(scope.row.lastLoginTime) }}</span>
            <span v-else class="text-muted">从未登录</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(scope.row)">重置密码</el-button>
            <el-button size="small" type="info" @click="handleViewRoles(scope.row)">查看角色</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="!!userForm.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!userForm.id">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="userForm.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="userForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="头像">
              <el-input v-model="userForm.avatar" placeholder="请输入头像URL" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="userForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色查看对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="用户角色"
      width="400px"
    >
      <div v-if="userRoles.length > 0">
        <el-tag 
          v-for="role in userRoles" 
          :key="role.id" 
          type="primary" 
          style="margin: 5px;"
        >
          {{ role.name }}
        </el-tag>
      </div>
      <div v-else class="text-muted">
        该用户暂未分配角色
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入用户"
      width="500px"
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
        <p>1. 请使用标准模板格式</p>
        <p>2. 用户名必须唯一，不能重复</p>
        <p>3. 密码为空时将使用默认密码：123456</p>
        <p>4. 状态默认为启用(1)</p>
        <el-button type="primary" link @click="downloadTemplate">下载模板</el-button>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImportSubmit">开始导入</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 响应式数据
const userList = ref([])
const selectedUsers = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleDialogVisible = ref(false)
const importDialogVisible = ref(false)
const userFormRef = ref()
const loading = ref(false)
const fileList = ref([])
const userRoles = ref([])

// 搜索表单
const searchForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  status: '',
  gender: ''
})

// 用户表单
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  gender: 0,
  status: 1,
  remark: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  remark: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/user/list', { params: searchForm })
    userList.value = response.data
    pagination.total = response.data.length
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getUserList()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 新增用户
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
  resetForm()
}

// 编辑用户
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  Object.assign(userForm, row)
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/api/user/${row.id}`)
    ElMessage.success('删除成功')
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const ids = selectedUsers.value.map(user => user.id)
    await request.post('/api/user/batch/delete', ids)
    ElMessage.success('批量删除成功')
    selectedUsers.value = []
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await request.put(`/api/user/${row.id}/status`, null, {
      params: { status: row.status }
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？重置后密码为：123456', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/api/user/${row.id}/password`)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('密码重置失败')
    }
  }
}

// 查看角色
const handleViewRoles = async (row) => {
  try {
    const response = await request.get(`/api/user/${row.id}/roles`)
    userRoles.value = response.data
    roleDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户角色失败')
  }
}

// 导入用户
const handleImport = () => {
  importDialogVisible.value = true
  fileList.value = []
}

// 导出用户
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 文件选择
const handleFileChange = (file) => {
  fileList.value = [file]
}

// 下载模板
const downloadTemplate = () => {
  ElMessage.info('模板下载功能开发中...')
}

// 提交导入
const handleImportSubmit = () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  ElMessage.info('导入功能开发中...')
  importDialogVisible.value = false
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 提交表单
const handleSubmit = async () => {
  try {
    await userFormRef.value.validate()
    
    if (userForm.id) {
      await request.put('/api/user/update', userForm)
      ElMessage.success('更新成功')
    } else {
      await request.post('/api/user/add', userForm)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: '',
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    avatar: '',
    gender: 0,
    status: 1,
    remark: ''
  })
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getUserList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  getUserList()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.operation-area {
  margin-bottom: 20px;
}

.pagination-area {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.text-muted {
  color: #999;
  text-align: center;
  padding: 20px;
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
  font-size: 14px;
}

.upload-demo {
  text-align: center;
}
</style> 