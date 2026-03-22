<template>
  <div class="reading-notes-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>📚 读书笔记</h1>
      <el-button type="primary" @click="showAddDialog = true">
        <i class="el-icon-plus"></i> 添加笔记
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索书名、作者或笔记内容"
            prefix-icon="el-icon-search"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterStatus" placeholder="阅读状态" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="未读" value="未读" />
            <el-option label="在读" value="在读" />
            <el-option label="已读" value="已读" />
            <el-option label="已弃" value="已弃" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort">
            <el-option label="创建时间" value="createdTime" />
            <el-option label="书名" value="bookTitle" />
            <el-option label="评分" value="rating" />
            <el-option label="阅读进度" value="currentPage" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button-group>
            <el-button :type="viewMode === 'card' ? 'primary' : ''" @click="viewMode = 'card'">
              <i class="el-icon-s-grid"></i> 卡片视图
            </el-button>
            <el-button :type="viewMode === 'table' ? 'primary' : ''" @click="viewMode = 'table'">
              <i class="el-icon-s-order"></i> 表格视图
            </el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </div>

    <!-- 统计信息 -->
    <div class="statistics">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.totalBooks }}</div>
              <div class="stat-label">总书籍数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.readingBooks }}</div>
              <div class="stat-label">正在阅读</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.finishedBooks }}</div>
              <div class="stat-label">已读完本</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.totalPages }}</div>
              <div class="stat-label">总阅读页数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 读书笔记列表 -->
    <div class="notes-list" v-loading="loading">
      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="card-view">
        <el-row :gutter="20">
          <el-col :span="8" v-for="note in filteredNotes" :key="note.id">
            <el-card class="note-card" shadow="hover">
              <div class="note-cover">
                <img :src="note.coverImage || '/src/assets/images/default-book.jpg'" :alt="note.bookTitle" />
                <div class="note-status" :class="getStatusClass(note.readingStatus)">
                  {{ note.readingStatus }}
                </div>
              </div>
              <div class="note-content">
                <h3 class="book-title">{{ note.bookTitle }}</h3>
                <p class="book-author">作者：{{ note.author || '未知' }}</p>
                <div class="book-rating" v-if="note.rating">
                  <el-rate v-model="note.rating" disabled show-score />
                </div>
                <div class="reading-progress">
                  <el-progress 
                    :percentage="getReadingProgress(note)" 
                    :color="getProgressColor(note)"
                    :stroke-width="8"
                  />
                  <span class="progress-text">{{ note.currentPage || 0 }} / {{ note.pageCount || 0 }} 页</span>
                </div>
                <div class="note-tags" v-if="note.tags">
                  <el-tag 
                    v-for="tag in note.tags.split(',')" 
                    :key="tag" 
                    size="small" 
                    style="margin-right: 5px;"
                  >
                    {{ tag.trim() }}
                  </el-tag>
                </div>
                <div class="note-actions">
                  <el-button size="mini" @click="editNote(note)">编辑</el-button>
                  <el-button size="mini" @click="updateProgress(note)">更新进度</el-button>
                  <el-button 
                    size="mini" 
                    :type="note.isFavorite ? 'danger' : 'default'"
                    @click="toggleFavorite(note)"
                  >
                    {{ note.isFavorite ? '取消收藏' : '收藏' }}
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 表格视图 -->
      <div v-else class="table-view">
        <el-table :data="filteredNotes" style="width: 100%">
          <el-table-column prop="bookTitle" label="书名" min-width="150" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="readingStatus" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.readingStatus)">
                {{ scope.row.readingStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="阅读进度" width="150">
            <template slot-scope="scope">
              <el-progress 
                :percentage="getReadingProgress(scope.row)" 
                :color="getProgressColor(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="120">
            <template slot-scope="scope">
              <el-rate v-model="scope.row.rating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="创建时间" width="150">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button size="mini" @click="editNote(scope.row)">编辑</el-button>
              <el-button size="mini" @click="updateProgress(scope.row)">进度</el-button>
              <el-button size="mini" type="danger" @click="deleteNote(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑读书笔记' : '添加读书笔记'" 
      :visible.sync="showAddDialog"
      width="60%"
    >
      <el-form :model="noteForm" :rules="noteRules" ref="noteForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="书名" prop="bookTitle">
              <el-input v-model="noteForm.bookTitle" placeholder="请输入书名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="noteForm.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="noteForm.isbn" placeholder="请输入ISBN号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总页数" prop="pageCount">
              <el-input-number v-model="noteForm.pageCount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="阅读状态" prop="readingStatus">
              <el-select v-model="noteForm.readingStatus" placeholder="请选择阅读状态" style="width: 100%">
                <el-option label="未读" value="未读" />
                <el-option label="在读" value="在读" />
                <el-option label="已读" value="已读" />
                <el-option label="已弃" value="已弃" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="noteForm.rating" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="noteForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图片" prop="coverImage">
              <el-input v-model="noteForm.coverImage" placeholder="请输入封面图片URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="noteForm.tags" placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
        <el-form-item label="笔记内容" prop="notesContent">
          <el-input
            v-model="noteForm.notesContent"
            type="textarea"
            :rows="6"
            placeholder="请输入笔记内容"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNote">保存</el-button>
      </div>
    </el-dialog>

    <!-- 更新进度对话框 -->
    <el-dialog title="更新阅读进度" :visible.sync="showProgressDialog" width="40%">
      <el-form :model="progressForm" ref="progressForm" label-width="100px">
        <el-form-item label="当前页数" prop="currentPage">
          <el-input-number 
            v-model="progressForm.currentPage" 
            :min="0" 
            :max="progressForm.pageCount" 
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="阅读时长(分钟)" prop="readingTime">
          <el-input-number v-model="progressForm.readingTime" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showProgressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProgress">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ReadingNotes',
  data() {
    return {
      loading: false,
      searchKeyword: '',
      filterStatus: '',
      sortBy: 'createdTime',
      viewMode: 'card',
      currentPage: 1,
      pageSize: 12,
      total: 0,
      showAddDialog: false,
      showProgressDialog: false,
      isEdit: false,
      notes: [],
      noteForm: {
        bookTitle: '',
        author: '',
        isbn: '',
        readingStatus: '未读',
        startDate: null,
        finishDate: null,
        rating: null,
        notesContent: '',
        tags: '',
        coverImage: '',
        pageCount: null,
        currentPage: 0,
        readingTime: 0,
        isFavorite: false
      },
      progressForm: {
        id: null,
        currentPage: 0,
        readingTime: 0,
        pageCount: 0
      },
      noteRules: {
        bookTitle: [
          { required: true, message: '请输入书名', trigger: 'blur' }
        ],
        readingStatus: [
          { required: true, message: '请选择阅读状态', trigger: 'change' }
        ]
      },
      statistics: {
        totalBooks: 0,
        readingBooks: 0,
        finishedBooks: 0,
        totalPages: 0
      }
    }
  },
  computed: {
    filteredNotes() {
      let notes = this.notes;
      
      if (this.searchKeyword) {
        notes = notes.filter(note => 
          note.bookTitle.includes(this.searchKeyword) ||
          (note.author && note.author.includes(this.searchKeyword)) ||
          (note.notesContent && note.notesContent.includes(this.searchKeyword))
        );
      }
      
      if (this.filterStatus) {
        notes = notes.filter(note => note.readingStatus === this.filterStatus);
      }
      
      // 排序
      notes.sort((a, b) => {
        switch (this.sortBy) {
          case 'bookTitle':
            return a.bookTitle.localeCompare(b.bookTitle);
          case 'rating':
            return (b.rating || 0) - (a.rating || 0);
          case 'currentPage':
            return (b.currentPage || 0) - (a.currentPage || 0);
          default:
            return new Date(b.createdTime) - new Date(a.createdTime);
        }
      });
      
      return notes;
    }
  },
  mounted() {
    this.loadNotes();
    this.loadStatistics();
  },
  methods: {
    async loadNotes() {
      this.loading = true;
      try {
        // 这里应该调用实际的API
        // const response = await this.$http.get(`/api/reading-notes/user/${this.$store.state.user.id}`);
        // this.notes = response.data;
        
        // 模拟数据
        this.notes = [
          {
            id: 1,
            bookTitle: '深入理解计算机系统',
            author: 'Randal E. Bryant',
            readingStatus: '在读',
            rating: 5,
            currentPage: 150,
            pageCount: 800,
            tags: '技术,计算机,系统',
            coverImage: '',
            createdTime: '2024-01-15T10:00:00',
            isFavorite: true
          },
          {
            id: 2,
            bookTitle: '百年孤独',
            author: '加西亚·马尔克斯',
            readingStatus: '已读',
            rating: 5,
            currentPage: 400,
            pageCount: 400,
            tags: '文学,魔幻现实主义',
            coverImage: '',
            createdTime: '2024-01-10T10:00:00',
            isFavorite: false
          }
        ];
        this.total = this.notes.length;
      } catch (error) {
        this.$message.error('加载读书笔记失败');
      } finally {
        this.loading = false;
      }
    },
    
    async loadStatistics() {
      try {
        // 这里应该调用实际的API
        // const response = await this.$http.get(`/api/reading-notes/user/${this.$store.state.user.id}/statistics`);
        // this.statistics = response.data;
        
        // 模拟统计数据
        this.statistics = {
          totalBooks: this.notes.length,
          readingBooks: this.notes.filter(n => n.readingStatus === '在读').length,
          finishedBooks: this.notes.filter(n => n.readingStatus === '已读').length,
          totalPages: this.notes.reduce((sum, n) => sum + (n.currentPage || 0), 0)
        };
      } catch (error) {
        this.$message.error('加载统计数据失败');
      }
    },
    
    handleSearch() {
      this.currentPage = 1;
    },
    
    handleFilter() {
      this.currentPage = 1;
    },
    
    handleSort() {
      this.currentPage = 1;
    },
    
    handleCurrentChange(page) {
      this.currentPage = page;
    },
    
    editNote(note) {
      this.isEdit = true;
      this.noteForm = { ...note };
      this.showAddDialog = true;
    },
    
    updateProgress(note) {
      this.progressForm = {
        id: note.id,
        currentPage: note.currentPage || 0,
        readingTime: 0,
        pageCount: note.pageCount || 0
      };
      this.showProgressDialog = true;
    },
    
    async saveNote() {
      try {
        this.$refs.noteForm.validate(async (valid) => {
          if (valid) {
            if (this.isEdit) {
              // 更新笔记
              // await this.$http.put(`/api/reading-notes/${this.noteForm.id}`, this.noteForm);
              const index = this.notes.findIndex(n => n.id === this.noteForm.id);
              if (index !== -1) {
                this.notes[index] = { ...this.noteForm };
              }
              this.$message.success('更新成功');
            } else {
              // 创建笔记
              // const response = await this.$http.post('/api/reading-notes', this.noteForm);
              // this.notes.unshift(response.data);
              const newNote = {
                ...this.noteForm,
                id: Date.now(),
                createdTime: new Date().toISOString()
              };
              this.notes.unshift(newNote);
              this.$message.success('创建成功');
            }
            this.showAddDialog = false;
            this.resetForm();
            this.loadStatistics();
          }
        });
      } catch (error) {
        this.$message.error('保存失败');
      }
    },
    
    async saveProgress() {
      try {
        // await this.$http.put(`/api/reading-notes/${this.progressForm.id}/progress`, {
        //   currentPage: this.progressForm.currentPage,
        //   readingTime: this.progressForm.readingTime
        // });
        
        const note = this.notes.find(n => n.id === this.progressForm.id);
        if (note) {
          note.currentPage = this.progressForm.currentPage;
          note.readingTime = (note.readingTime || 0) + this.progressForm.readingTime;
        }
        
        this.$message.success('进度更新成功');
        this.showProgressDialog = false;
        this.loadStatistics();
      } catch (error) {
        this.$message.error('更新进度失败');
      }
    },
    
    async deleteNote(note) {
      try {
        await this.$confirm('确定要删除这本读书笔记吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        // await this.$http.delete(`/api/reading-notes/${note.id}`);
        const index = this.notes.findIndex(n => n.id === note.id);
        if (index !== -1) {
          this.notes.splice(index, 1);
        }
        
        this.$message.success('删除成功');
        this.loadStatistics();
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败');
        }
      }
    },
    
    async toggleFavorite(note) {
      try {
        // await this.$http.put(`/api/reading-notes/${note.id}/favorite`);
        note.isFavorite = !note.isFavorite;
        this.$message.success(note.isFavorite ? '已收藏' : '已取消收藏');
      } catch (error) {
        this.$message.error('操作失败');
      }
    },
    
    resetForm() {
      this.noteForm = {
        bookTitle: '',
        author: '',
        isbn: '',
        readingStatus: '未读',
        startDate: null,
        finishDate: null,
        rating: null,
        notesContent: '',
        tags: '',
        coverImage: '',
        pageCount: null,
        currentPage: 0,
        readingTime: 0,
        isFavorite: false
      };
      this.isEdit = false;
      this.$refs.noteForm.resetFields();
    },
    
    getStatusClass(status) {
      const statusMap = {
        '未读': 'status-unread',
        '在读': 'status-reading',
        '已读': 'status-finished',
        '已弃': 'status-abandoned'
      };
      return statusMap[status] || 'status-unread';
    },
    
    getStatusType(status) {
      const statusMap = {
        '未读': 'info',
        '在读': 'warning',
        '已读': 'success',
        '已弃': 'danger'
      };
      return statusMap[status] || 'info';
    },
    
    getReadingProgress(note) {
      if (!note.pageCount) return 0;
      return Math.round((note.currentPage || 0) / note.pageCount * 100);
    },
    
    getProgressColor(note) {
      const progress = this.getReadingProgress(note);
      if (progress >= 80) return '#67C23A';
      if (progress >= 50) return '#E6A23C';
      return '#F56C6C';
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString();
    }
  }
}
</script>

<style scoped>
.reading-notes-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.search-filter {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.statistics {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.notes-list {
  margin-bottom: 20px;
}

.card-view {
  margin-bottom: 20px;
}

.note-card {
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.note-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.note-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.note-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.status-unread { background-color: #909399; }
.status-reading { background-color: #E6A23C; }
.status-finished { background-color: #67C23A; }
.status-abandoned { background-color: #F56C6C; }

.note-content {
  padding: 20px;
}

.book-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
}

.book-rating {
  margin-bottom: 15px;
}

.reading-progress {
  margin-bottom: 15px;
}

.progress-text {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.note-tags {
  margin-bottom: 15px;
}

.note-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.table-view {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style> 