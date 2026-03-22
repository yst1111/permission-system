<template>
  <div class="diary-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>📝 我的日记</h1>
      <div class="header-actions">
        <el-button @click="exportDiaries" type="success" size="small">
          <i class="el-icon-download"></i> 导出
        </el-button>
        <el-button @click="importDiaries" type="warning" size="small">
          <i class="el-icon-upload2"></i> 导入
        </el-button>
        <el-button type="primary" @click="showAddDialog = true">
          <i class="el-icon-plus"></i> 写日记
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索日记标题或内容"
            prefix-icon="el-icon-search"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterMood" placeholder="心情状态" @change="handleFilter" clearable>
            <el-option label="全部" value="" />
            <el-option label="开心" value="开心" />
            <el-option label="平静" value="平静" />
            <el-option label="难过" value="难过" />
            <el-option label="愤怒" value="愤怒" />
            <el-option label="兴奋" value="兴奋" />
            <el-option label="焦虑" value="焦虑" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateFilter"
            style="width: 100%"
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterTags" placeholder="标签筛选" @change="handleFilter" clearable multiple>
            <el-option 
              v-for="tag in availableTags" 
              :key="tag" 
              :label="tag" 
              :value="tag"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button-group>
            <el-button :type="viewMode === 'calendar' ? 'primary' : ''" @click="viewMode = 'calendar'">
              <i class="el-icon-date"></i> 日历视图
            </el-button>
            <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">
              <i class="el-icon-s-order"></i> 列表视图
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
              <div class="stat-number">{{ statistics.totalEntries }}</div>
              <div class="stat-label">总日记数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.totalWords }}</div>
              <div class="stat-label">总字数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.thisMonth }}</div>
              <div class="stat-label">本月日记</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ statistics.favorites }}</div>
              <div class="stat-label">收藏日记</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 日记内容 -->
    <div class="diary-content" v-loading="loading">
      <!-- 日历视图 -->
      <div v-if="viewMode === 'calendar'" class="calendar-view">
        <el-calendar v-model="currentDate">
          <template slot="dateCell" slot-scope="{date, data}">
            <div class="calendar-cell">
              <span>{{ data.day.split('-').slice(2).join('') }}</span>
              <div v-if="getDiaryForDate(data.day)" class="diary-indicator">
                <i class="el-icon-edit"></i>
              </div>
            </div>
          </template>
        </el-calendar>
        
        <!-- 选中日期的日记 -->
        <div v-if="selectedDateDiary" class="selected-date-diary">
          <el-card>
            <div slot="header">
              <span>{{ formatDate(selectedDateDiary.entryDate) }} 的日记</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="editDiary(selectedDateDiary)">
                编辑
              </el-button>
            </div>
            <div class="diary-preview">
              <h3>{{ selectedDateDiary.title || '无标题' }}</h3>
              <div class="diary-meta">
                <el-tag :type="getMoodType(selectedDateDiary.mood)">{{ selectedDateDiary.mood }}</el-tag>
                <span v-if="selectedDateDiary.weather" class="weather">{{ selectedDateDiary.weather }}</span>
                <span v-if="selectedDateDiary.location" class="location">{{ selectedDateDiary.location }}</span>
              </div>
              <p class="diary-content-text">{{ selectedDateDiary.content }}</p>
              <div class="diary-tags" v-if="selectedDateDiary.tags">
                <el-tag 
                  v-for="tag in selectedDateDiary.tags.split(',')" 
                  :key="tag" 
                  size="small" 
                  style="margin-right: 5px;"
                >
                  {{ tag.trim() }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <el-timeline>
          <el-timeline-item
            v-for="diary in filteredDiaries"
            :key="diary.id"
            :timestamp="formatDate(diary.entryDate)"
            :type="getTimelineType(diary.mood)"
          >
            <el-card class="diary-card">
              <div class="diary-header">
                <h3 class="diary-title">{{ diary.title || '无标题' }}</h3>
                <div class="diary-actions">
                  <el-button size="mini" @click="editDiary(diary)">编辑</el-button>
                  <el-button size="mini" type="danger" @click="deleteDiary(diary)">删除</el-button>
                </div>
              </div>
              <div class="diary-meta">
                <el-tag :type="getMoodType(diary.mood)">{{ diary.mood }}</el-tag>
                <span v-if="diary.weather" class="weather">{{ diary.weather }}</span>
                <span v-if="diary.location" class="location">{{ diary.location }}</span>
                <span class="word-count">{{ diary.wordCount || 0 }} 字</span>
              </div>
              <p class="diary-content-text">{{ diary.content }}</p>
              <div class="diary-tags" v-if="diary.tags">
                <el-tag 
                  v-for="tag in diary.tags.split(',')" 
                  :key="tag" 
                  size="small" 
                  style="margin-right: 5px;"
                >
                  {{ tag.trim() }}
                </el-tag>
              </div>
              <div class="diary-footer">
                <el-button 
                  size="mini" 
                  :type="diary.isFavorite ? 'danger' : 'default'"
                  @click="toggleFavorite(diary)"
                >
                  {{ diary.isFavorite ? '取消收藏' : '收藏' }}
                </el-button>
                <span class="diary-time">{{ formatTime(diary.createdTime) }}</span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑日记' : '写日记'" 
      :visible.sync="showAddDialog"
      width="70%"
    >
      <el-form :model="diaryForm" :rules="diaryRules" ref="diaryForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="日记日期" prop="entryDate">
              <el-date-picker
                v-model="diaryForm.entryDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日记标题" prop="title">
              <el-input v-model="diaryForm.title" placeholder="请输入日记标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="心情状态" prop="mood">
              <el-select v-model="diaryForm.mood" placeholder="请选择心情" style="width: 100%">
                <el-option label="开心" value="开心" />
                <el-option label="平静" value="平静" />
                <el-option label="难过" value="难过" />
                <el-option label="愤怒" value="愤怒" />
                <el-option label="兴奋" value="兴奋" />
                <el-option label="焦虑" value="焦虑" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="天气" prop="weather">
              <el-input v-model="diaryForm.weather" placeholder="请输入天气" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地点" prop="location">
              <el-input v-model="diaryForm.location" placeholder="请输入地点" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签" prop="tags">
          <div class="tags-input-container">
            <el-select
              v-model="selectedTags"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="请选择或输入标签"
              @change="handleTagsChange"
              style="width: 100%"
            >
              <el-option
                v-for="tag in availableTags"
                :key="tag"
                :label="tag"
                :value="tag"
              />
            </el-select>
            <div class="selected-tags-display" v-if="diaryForm.tags">
              <el-tag
                v-for="tag in diaryForm.tags.split(',').filter(t => t.trim())"
                :key="tag.trim()"
                closable
                @close="removeTag(tag.trim())"
                style="margin: 5px 5px 5px 0;"
              >
                {{ tag.trim() }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="日记内容" prop="content">
          <el-input
            v-model="diaryForm.content"
            type="textarea"
            :rows="12"
            placeholder="写下今天的故事..."
            @input="updateWordCount"
          />
          <div class="word-count-display">
            字数：{{ diaryForm.wordCount || 0 }}
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否私密">
              <el-switch v-model="diaryForm.isPrivate" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否收藏">
              <el-switch v-model="diaryForm.isFavorite" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveDiary">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Diary',
  data() {
    return {
      loading: false,
      searchKeyword: '',
      filterMood: '',
      filterTags: [],
      dateRange: null,
      viewMode: 'calendar',
      currentDate: new Date(),
      selectedDateDiary: null,
      showAddDialog: false,
      isEdit: false,
      diaries: [],
      diaryForm: {
        userId: null, // 将从用户状态获取
        entryDate: new Date().toISOString().split('T')[0], // 使用ISO日期格式
        title: '',
        content: '',
        mood: '平静',
        weather: '',
        location: '',
        tags: '',
        isPrivate: true,
        isFavorite: false,
        wordCount: 0,
        readingTime: 0
      },
      diaryRules: {
        entryDate: [
          { required: true, message: '请选择日期', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入日记内容', trigger: 'blur' }
        ],
        mood: [
          { required: true, message: '请选择心情状态', trigger: 'change' }
        ]
      },
      statistics: {
        totalEntries: 0,
        totalWords: 0,
        thisMonth: 0,
        favorites: 0
      },
      currentUserId: null, // 将从用户状态获取
      availableTags: ['生活', '工作', '学习', '旅行', '美食', '运动', '阅读', '电影', '音乐', '其他'],
      selectedTags: []
    }
  },
  computed: {
    filteredDiaries() {
      let diaries = this.diaries;
      
      if (this.searchKeyword) {
        diaries = diaries.filter(diary => 
          (diary.title && diary.title.includes(this.searchKeyword)) ||
          (diary.content && diary.content.includes(this.searchKeyword))
        );
      }
      
      if (this.filterMood) {
        diaries = diaries.filter(diary => diary.mood === this.filterMood);
      }

      if (this.filterTags && this.filterTags.length > 0) {
        diaries = diaries.filter(diary => {
          const diaryTags = diary.tags ? diary.tags.split(',').map(tag => tag.trim()) : [];
          return this.filterTags.every(tag => diaryTags.includes(tag));
        });
      }
      
      if (this.dateRange && this.dateRange.length === 2) {
        const startDate = this.dateRange[0];
        const endDate = this.dateRange[1];
        diaries = diaries.filter(diary => {
          const diaryDate = new Date(diary.entryDate);
          return diaryDate >= startDate && diaryDate <= endDate;
        });
      }
      
      // 按日期倒序排列
      diaries.sort((a, b) => new Date(b.entryDate) - new Date(a.entryDate));
      
      return diaries;
    }
  },
  async mounted() {
    await this.initializeUser();
    await this.loadDiaries();
    await this.loadStatistics();
  },
  methods: {
    async initializeUser() {
      // 从localStorage或用户状态获取用户信息
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        try {
          const user = JSON.parse(userInfo);
          this.currentUserId = user.id || 1; // 默认用户ID为1
          this.diaryForm.userId = this.currentUserId;
        } catch (error) {
          console.error('解析用户信息失败:', error);
          this.currentUserId = 1;
          this.diaryForm.userId = 1;
        }
      } else {
        // 如果没有用户信息，使用默认值
        this.currentUserId = 1;
        this.diaryForm.userId = 1;
      }
    },

    async loadDiaries() {
      if (!this.currentUserId) {
        this.$message.warning('请先登录');
        return;
      }

      this.loading = true;
      try {
        console.log('🔍 正在加载用户日记，用户ID:', this.currentUserId);
        const response = await request.get(`/api/diary-entries/user/${this.currentUserId}`);
        console.log('📝 日记加载响应:', response);
        
        if (response.data && Array.isArray(response.data)) {
          this.diaries = response.data.map(diary => ({
            ...diary,
            entryDate: diary.entryDate || diary.createdTime?.split('T')[0] || new Date().toISOString().split('T')[0],
            tags: diary.tags || '',
            isPrivate: diary.isPrivate !== undefined ? diary.isPrivate : true,
            isFavorite: diary.isFavorite !== undefined ? diary.isFavorite : false,
            wordCount: diary.wordCount || (diary.content ? diary.content.length : 0)
          }));
          console.log('✅ 日记数据处理完成，共', this.diaries.length, '篇');
        } else {
          console.warn('⚠️ 响应数据格式异常:', response.data);
          this.diaries = [];
        }
      } catch (error) {
        console.error('❌ 加载日记失败:', error);
        this.$message.error('加载日记失败，使用模拟数据');
        this.loadMockData();
      } finally {
        this.loading = false;
      }
    },
    
    loadMockData() {
      this.diaries = [
        {
          id: 1,
          userId: this.currentUserId,
          entryDate: '2024-01-15',
          title: '美好的一天',
          content: '今天天气很好，心情也很愉快。早上去了公园散步，看到了很多人在晨练，感受到了生活的美好。下午在家里看书，学到了很多新知识。晚上和朋友一起吃饭，聊得很开心。',
          mood: '开心',
          weather: '晴天',
          location: '家里',
          tags: '生活,朋友,学习',
          isPrivate: false,
          isFavorite: true,
          wordCount: 89,
          readingTime: 1,
          createdTime: '2024-01-15T20:00:00',
          updatedTime: '2024-01-15T20:00:00'
        },
        {
          id: 2,
          userId: this.currentUserId,
          entryDate: '2024-01-14',
          title: '工作感悟',
          content: '今天在工作中遇到了一些挑战，但是通过团队合作，我们成功地解决了问题。这让我深刻体会到团队协作的重要性，也学到了很多新的技能。',
          mood: '平静',
          weather: '阴天',
          location: '公司',
          tags: '工作,团队,学习',
          isPrivate: true,
          isFavorite: false,
          wordCount: 76,
          readingTime: 1,
          createdTime: '2024-01-14T19:00:00',
          updatedTime: '2024-01-14T19:00:00'
        }
      ];
    },
    
    async loadStatistics() {
      if (!this.currentUserId) return;

      try {
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1;
        
        const response = await request.get(`/api/diary-entries/user/${this.currentUserId}/statistics`, {
          params: { year, month }
        });
        
        if (response.data && Array.isArray(response.data)) {
          // 计算统计数据
          this.statistics = {
            totalEntries: this.diaries.length,
            totalWords: this.diaries.reduce((sum, d) => sum + (d.wordCount || 0), 0),
            thisMonth: response.data.length,
            favorites: this.diaries.filter(d => d.isFavorite).length
          };
        } else {
          this.calculateLocalStatistics();
        }
      } catch (error) {
        console.error('加载统计数据失败:', error);
        this.calculateLocalStatistics();
      }
    },
    
    calculateLocalStatistics() {
      this.statistics = {
        totalEntries: this.diaries.length,
        totalWords: this.diaries.reduce((sum, d) => sum + (d.wordCount || 0), 0),
        thisMonth: this.diaries.filter(d => {
          const diaryDate = new Date(d.entryDate);
          const now = new Date();
          return diaryDate.getMonth() === now.getMonth() && diaryDate.getFullYear() === now.getFullYear();
        }).length,
        favorites: this.diaries.filter(d => d.isFavorite).length
      };
    },
    
    handleSearch() {
      // 搜索逻辑已在computed中处理
    },
    
    handleFilter() {
      // 筛选逻辑已在computed中处理
    },
    
    handleDateFilter() {
      // 日期筛选逻辑已在computed中处理
    },
    
    getDiaryForDate(dateString) {
      const date = dateString.split('T')[0];
      return this.diaries.find(diary => diary.entryDate === date);
    },
    
    editDiary(diary) {
      this.isEdit = true;
      this.diaryForm = { 
        ...diary,
        entryDate: diary.entryDate || diary.createdTime?.split('T')[0] || new Date().toISOString().split('T')[0]
      };
      // 设置选中的标签
      this.selectedTags = diary.tags ? diary.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : [];
      this.showAddDialog = true;
    },
    
    async saveDiary() {
      try {
        this.$refs.diaryForm.validate(async (valid) => {
          if (valid) {
            // 准备提交的数据
            const submitData = {
              ...this.diaryForm,
              userId: this.currentUserId,
              entryDate: this.diaryForm.entryDate,
              tags: this.diaryForm.tags,
              wordCount: this.diaryForm.content ? this.diaryForm.content.length : 0,
              readingTime: Math.ceil((this.diaryForm.content ? this.diaryForm.content.length : 0) / 200) // 假设每分钟读200字
            };

            if (this.isEdit) {
              // 更新日记
              const response = await request.put(`/api/diary-entries/${this.diaryForm.id}`, submitData);
              if (response.data) {
                const index = this.diaries.findIndex(d => d.id === this.diaryForm.id);
                if (index !== -1) {
                  this.diaries[index] = { ...response.data };
                }
                this.$message.success('更新成功');
              }
            } else {
              // 创建日记
              const response = await request.post('/api/diary-entries', submitData);
              if (response.data) {
                this.diaries.unshift(response.data);
                this.$message.success('创建成功');
              }
            }
            
            this.showAddDialog = false;
            this.resetForm();
            await this.loadStatistics();
          }
        });
      } catch (error) {
        console.error('保存失败:', error);
        this.$message.error('保存失败: ' + (error.response?.data?.message || error.message || '未知错误'));
      }
    },
    
    async deleteDiary(diary) {
      try {
        await this.$confirm('确定要删除这篇日记吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        const response = await request.delete(`/api/diary-entries/${diary.id}`);
        if (response.data) {
          const index = this.diaries.findIndex(d => d.id === diary.id);
          if (index !== -1) {
            this.diaries.splice(index, 1);
          }
          this.$message.success('删除成功');
          await this.loadStatistics();
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error);
          this.$message.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'));
        }
      }
    },
    
    async toggleFavorite(diary) {
      try {
        const response = await request.put(`/api/diary-entries/${diary.id}/favorite`);
        if (response.data) {
          diary.isFavorite = !diary.isFavorite;
          this.$message.success(diary.isFavorite ? '已收藏' : '已取消收藏');
          await this.loadStatistics();
        }
      } catch (error) {
        console.error('操作失败:', error);
        this.$message.error('操作失败: ' + (error.response?.data?.message || error.message || '未知错误'));
      }
    },
    
    updateWordCount() {
      this.diaryForm.wordCount = this.diaryForm.content ? this.diaryForm.content.length : 0;
      this.diaryForm.readingTime = Math.ceil(this.diaryForm.wordCount / 200);
    },
    
    resetForm() {
      this.diaryForm = {
        userId: this.currentUserId,
        entryDate: new Date().toISOString().split('T')[0],
        title: '',
        content: '',
        mood: '平静',
        weather: '',
        location: '',
        tags: '',
        isPrivate: true,
        isFavorite: false,
        wordCount: 0,
        readingTime: 0
      };
      this.isEdit = false;
      this.selectedTags = [];
      if (this.$refs.diaryForm) {
        this.$refs.diaryForm.resetFields();
      }
    },
    
    getMoodType(mood) {
      const moodMap = {
        '开心': 'success',
        '平静': 'info',
        '难过': 'warning',
        '愤怒': 'danger',
        '兴奋': 'success',
        '焦虑': 'warning',
        '其他': 'info'
      };
      return moodMap[mood] || 'info';
    },
    
    getTimelineType(mood) {
      const moodMap = {
        '开心': 'success',
        '平静': 'primary',
        '难过': 'warning',
        '愤怒': 'danger',
        '兴奋': 'success',
        '焦虑': 'warning',
        '其他': 'info'
      };
      return moodMap[mood] || 'primary';
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
    },
    
    formatTime(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleTimeString();
    },

    handleTagsChange(value) {
      this.diaryForm.tags = value.join(',');
    },

    removeTag(tag) {
      if (this.diaryForm.tags) {
        const tags = this.diaryForm.tags.split(',').filter(t => t.trim() !== tag.trim());
        this.diaryForm.tags = tags.join(',');
        // 同时更新selectedTags
        this.selectedTags = this.selectedTags.filter(t => t !== tag);
      }
    },

    async exportDiaries() {
      if (!this.currentUserId) {
        this.$message.warning('请先登录');
        return;
      }

      try {
        const response = await request.get(`/api/diary-entries/user/${this.currentUserId}/export`, {
          responseType: 'blob'
        });
        const blob = new Blob([response.data], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `my_diaries_${new Date().toISOString().slice(0, 10)}.json`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        this.$message.success('日记已导出');
      } catch (error) {
        console.error('导出日记失败:', error);
        this.$message.error('导出日记失败: ' + (error.response?.data?.message || error.message || '未知错误'));
      }
    },

    async importDiaries() {
      if (!this.currentUserId) {
        this.$message.warning('请先登录');
        return;
      }

      const input = document.createElement('input');
      input.type = 'file';
      input.accept = 'application/json';
      input.onchange = async (event) => {
        const file = event.target.files[0];
        if (file) {
          try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', this.currentUserId);
            
            const response = await request.post('/api/diary-entries/import', formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            });
            
            if (response.data && Array.isArray(response.data)) {
              this.diaries = response.data;
              this.$message.success('日记导入成功');
              await this.loadStatistics();
            }
          } catch (error) {
            console.error('导入日记失败:', error);
            this.$message.error('导入日记失败: ' + (error.response?.data?.message || error.message || '未知错误'));
          } finally {
            input.remove();
          }
        }
      };
      input.click();
    }
  }
}
</script>

<style scoped>
.diary-container {
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

.header-actions {
  display: flex;
  gap: 10px;
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

.diary-content {
  margin-bottom: 20px;
}

.calendar-view {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.calendar-cell {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.diary-indicator {
  position: absolute;
  top: 2px;
  right: 2px;
  color: #409EFF;
  font-size: 12px;
}

.selected-date-diary {
  margin-top: 20px;
}

.diary-preview h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.diary-meta {
  margin-bottom: 15px;
}

.diary-meta .weather,
.diary-meta .location {
  margin-left: 15px;
  color: #909399;
  font-size: 14px;
}

.diary-content-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.diary-tags {
  margin-bottom: 15px;
}

.list-view {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.diary-card {
  border: none;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);
}

.diary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.diary-title {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.diary-actions {
  display: flex;
  gap: 10px;
}

.diary-meta {
  margin-bottom: 15px;
}

.diary-meta .weather,
.diary-meta .location,
.diary-meta .word-count {
  margin-left: 15px;
  color: #909399;
  font-size: 14px;
}

.diary-content-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
  max-height: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

.diary-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #EBEEF5;
}

.diary-time {
  color: #909399;
  font-size: 12px;
}

.word-count-display {
  text-align: right;
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.dialog-footer {
  text-align: right;
}

.tags-input-container {
  position: relative;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding: 5px 0;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  background-color: #F5F7FA;
  min-height: 38px;
  box-sizing: border-box;
}

.tags-input-container .el-select {
  flex: 1;
  margin-right: 10px;
}

.tags-input-container .el-select .el-input__inner {
  border: none;
  padding-left: 0;
  padding-right: 0;
  height: 38px;
}

.tags-input-container .el-select .el-input__inner:focus {
  border-color: #DCDFE6;
  box-shadow: none;
}

.tags-input-container .el-select .el-input__suffix {
  display: none;
}

.tags-input-container .el-select .el-input__icon {
  display: none;
}

.selected-tags-display {
  display: flex;
  flex-wrap: wrap;
  margin-left: 10px;
}

.selected-tags-display .el-tag {
  margin-bottom: 5px;
}
</style> 