<template>
  <div class="conflict-detection-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2><i class="el-icon-warning"></i> 课程冲突检测</h2>
      <div class="toolbar">
        <el-select v-model="selectedSemester" placeholder="选择学期" @change="handleSemesterChange" clearable>
          <el-option v-for="semester in semesters" :key="semester" :value="semester" :label="semester"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="detectConflicts">开始检测</el-button>
        <el-button type="success" icon="el-icon-refresh" @click="refreshData">刷新</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalConflicts }}</div>
            <div class="stat-label">总冲突数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.teacherConflicts }}</div>
            <div class="stat-label">教师冲突</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.classroomConflicts }}</div>
            <div class="stat-label">教室冲突</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.timeConflicts }}</div>
            <div class="stat-label">时间冲突</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 冲突类型筛选 -->
    <el-card class="filter-card" shadow="hover">
      <el-radio-group v-model="filterType" @change="applyFilter">
        <el-radio-button label="ALL">全部冲突</el-radio-button>
        <el-radio-button label="TEACHER">教师冲突</el-radio-button>
        <el-radio-button label="CLASSROOM">教室冲突</el-radio-button>
        <el-radio-button label="TIME">时间冲突</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 冲突列表 -->
    <el-card class="table-card" shadow="hover">
      <div class="card-header">
        <h3><i class="el-icon-s-order"></i> 冲突详情列表</h3>
        <span class="conflict-count">共 {{ displayConflicts.length }} 个冲突</span>
      </div>
      
      <el-table :data="displayConflicts" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
            <div class="expand-content">
              <h4>涉及的课程：</h4>
              <el-table :data="props.row.coursePlans" size="small">
                <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
                <el-table-column prop="teacherName" label="教师" width="100"></el-table-column>
                <el-table-column prop="className" label="班级" width="120"></el-table-column>
                <el-table-column prop="classroomName" label="教室" width="120"></el-table-column>
                <el-table-column prop="classTime" label="上课时间"></el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="conflictType" label="冲突类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getConflictTypeTag(scope.row.conflictType)">
              {{ getConflictTypeLabel(scope.row.conflictType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="conflictLevel" label="冲突级别" width="100">
          <template slot-scope="scope">
            <el-tag :type="getConflictLevelTag(scope.row.conflictLevel)" effect="dark">
              {{ getConflictLevelLabel(scope.row.conflictLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="冲突描述" min-width="250"></el-table-column>
        <el-table-column prop="conflictTime" label="冲突时间" width="150"></el-table-column>
        <el-table-column prop="coursePlans" label="涉及课程" width="100">
          <template slot-scope="scope">
            <el-tag type="info">{{ scope.row.coursePlans.length }} 个</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewSuggestion(scope.row)">
              <i class="el-icon-info"></i> 建议
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 解决建议对话框 -->
    <el-dialog title="解决建议" :visible.sync="suggestionVisible" width="600px">
      <div class="suggestion-content" v-if="currentConflict">
        <el-alert :title="currentConflict.description" type="warning" :closable="false" show-icon></el-alert>
        
        <div class="suggestion-section">
          <h4><i class="el-icon-lightbulb"></i> 建议方案</h4>
          <p>{{ currentConflict.suggestion }}</p>
        </div>
        
        <div class="suggestion-section">
          <h4><i class="el-icon-document"></i> 涉及课程</h4>
          <el-table :data="currentConflict.coursePlans" size="small">
            <el-table-column prop="courseName" label="课程" width="120"></el-table-column>
            <el-table-column prop="teacherName" label="教师" width="100"></el-table-column>
            <el-table-column prop="className" label="班级" width="100"></el-table-column>
            <el-table-column prop="classTime" label="时间"></el-table-column>
          </el-table>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="suggestionVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleResolve">标记已解决</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ConflictDetection",
  data() {
    return {
      loading: false,
      selectedSemester: '',
      filterType: 'ALL',
      
      // 数据
      semesters: [],
      conflicts: [],
      displayConflicts: [],
      
      // 对话框
      suggestionVisible: false,
      currentConflict: null,
      
      // 统计数据
      stats: {
        totalConflicts: 0,
        teacherConflicts: 0,
        classroomConflicts: 0,
        timeConflicts: 0,
        highLevelConflicts: 0,
        mediumLevelConflicts: 0
      }
    };
  },
  
  mounted() {
    this.initData();
  },
  
  methods: {
    // 初始化数据
    initData() {
      this.getSemesters();
      this.detectConflicts();
    },
    
    // 获取学期列表
    getSemesters() {
      this.$axios.get("http://localhost:8080/semester")
        .then(res => {
          this.semesters = res.data.data;
          if (this.semesters.length > 0) {
            this.selectedSemester = this.semesters[0];
          }
        })
        .catch(error => {
          console.error("获取学期列表失败:", error);
        });
    },
    
    // 检测冲突
    detectConflicts() {
      this.loading = true;
      
      const params = {};
      if (this.selectedSemester) {
        params.semester = this.selectedSemester;
      }
      
      this.$axios.get('http://localhost:8080/conflict/detect', { params })
        .then(response => {
          if (response.data.code === 200) {
            this.conflicts = response.data.data || [];
            this.applyFilter();
            this.getStatsSummary();
            
            if (this.conflicts.length === 0) {
              this.$message.success('未检测到冲突！');
            } else {
              this.$message.warning(`检测到 ${this.conflicts.length} 个冲突`);
            }
          } else {
            this.$message.error(response.data.msg || '检测失败');
          }
        })
        .catch(error => {
          console.error('检测失败:', error);
          this.$message.error('检测失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 获取统计摘要
    getStatsSummary() {
      const params = {};
      if (this.selectedSemester) {
        params.semester = this.selectedSemester;
      }
      
      this.$axios.get('http://localhost:8080/conflict/summary', { params })
        .then(response => {
          if (response.data.code === 200) {
            this.stats = response.data.data;
          }
        })
        .catch(error => {
          console.error('获取统计失败:', error);
        });
    },
    
    // 应用筛选
    applyFilter() {
      if (this.filterType === 'ALL') {
        this.displayConflicts = this.conflicts;
      } else {
        this.displayConflicts = this.conflicts.filter(c => c.conflictType === this.filterType);
      }
    },
    
    // 刷新数据
    refreshData() {
      this.detectConflicts();
      this.$message.success('数据已刷新');
    },
    
    // 学期变化
    handleSemesterChange() {
      this.detectConflicts();
    },
    
    // 查看建议
    viewSuggestion(row) {
      this.currentConflict = row;
      this.suggestionVisible = true;
    },
    
    // 处理解决
    handleResolve() {
      this.$message.success('已标记为解决');
      this.suggestionVisible = false;
    },
    
    // 获取冲突类型标签
    getConflictTypeTag(type) {
      const tags = {
        'TEACHER': 'danger',
        'CLASSROOM': 'warning',
        'TIME': 'info'
      };
      return tags[type] || 'info';
    },
    
    // 获取冲突类型标签文本
    getConflictTypeLabel(type) {
      const labels = {
        'TEACHER': '教师冲突',
        'CLASSROOM': '教室冲突',
        'TIME': '时间冲突'
      };
      return labels[type] || '未知';
    },
    
    // 获取冲突级别标签
    getConflictLevelTag(level) {
      const tags = {
        'HIGH': 'danger',
        'MEDIUM': 'warning',
        'LOW': 'success'
      };
      return tags[level] || 'info';
    },
    
    // 获取冲突级别标签文本
    getConflictLevelLabel(level) {
      const labels = {
        'HIGH': '高',
        'MEDIUM': '中',
        'LOW': '低'
      };
      return labels[level] || '未知';
    }
  }
}
</script>

<style scoped lang="less">
.conflict-detection-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
    
    i {
      margin-right: 10px;
      color: #F56C6C;
    }
  }
  
  .toolbar {
    display: flex;
    gap: 10px;
  }
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    i {
      font-size: 28px;
      color: white;
    }
  }
  
  .stat-info {
    flex: 1;
  }
  
  .stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 5px;
  }
  
  .stat-label {
    font-size: 14px;
    color: #909399;
  }
}

.filter-card {
  margin-bottom: 20px;
  text-align: center;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      color: #303133;
      
      i {
        margin-right: 8px;
        color: #409EFF;
      }
    }
    
    .conflict-count {
      color: #909399;
      font-size: 14px;
    }
  }
}

.expand-content {
  padding: 20px;
  background: #f5f7fa;
  
  h4 {
    margin: 0 0 15px 0;
    color: #303133;
  }
}

.suggestion-content {
  .suggestion-section {
    margin-top: 20px;
    
    h4 {
      margin: 0 0 10px 0;
      color: #303133;
      
      i {
        margin-right: 5px;
        color: #409EFF;
      }
    }
    
    p {
      margin: 0;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 4px;
      color: #606266;
      line-height: 1.6;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    
    .toolbar {
      width: 100%;
      flex-wrap: wrap;
    }
  }
  
  .stats-container {
    grid-template-columns: 1fr;
  }
}
</style>
