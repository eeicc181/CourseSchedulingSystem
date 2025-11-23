<template>
  <div class="teacher-workload-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2><i class="el-icon-user"></i> 教师工作量统计</h2>
      <div class="toolbar">
        <el-select v-model="selectedSemester" placeholder="选择学期" @change="handleSemesterChange" clearable>
          <el-option v-for="semester in semesters" :key="semester" :value="semester" :label="semester"></el-option>
        </el-select>
        <el-input v-model="searchKeyword" placeholder="搜索教师姓名" @input="handleSearch" clearable style="width: 200px;">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
        <el-button type="primary" icon="el-icon-refresh" @click="refreshData">刷新</el-button>
        <el-button type="success" icon="el-icon-download" @click="exportReport">导出报表</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalTeachers }}</div>
            <div class="stat-label">教师总数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.activeTeachers }}</div>
            <div class="stat-label">授课教师</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.averageHours }}</div>
            <div class="stat-label">平均周课时</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.overload }}</div>
            <div class="stat-label">超负荷教师</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <el-card class="chart-card" shadow="hover">
        <div class="card-header">
          <h3><i class="el-icon-s-data"></i> 工作量分布统计</h3>
        </div>
        <div id="workloadDistChart" class="chart"></div>
      </el-card>

      <el-card class="chart-card" shadow="hover">
        <div class="card-header">
          <h3><i class="el-icon-pie-chart"></i> 工作负荷等级分布</h3>
        </div>
        <div id="workloadPieChart" class="chart"></div>
      </el-card>
    </div>

    <!-- 教师列表 -->
    <el-card class="table-card" shadow="hover">
      <div class="card-header">
        <h3><i class="el-icon-s-order"></i> 教师工作量详情</h3>
      </div>
      
      <el-table :data="displayTeachers" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="teacherNo" label="教师编号" width="120"></el-table-column>
        <el-table-column prop="teacherName" label="教师姓名" width="120"></el-table-column>
        <el-table-column prop="department" label="所属部门" width="150"></el-table-column>
        <el-table-column prop="title" label="职称" width="100"></el-table-column>
        <el-table-column prop="weeklyHours" label="周课时" width="100" sortable>
          <template slot-scope="scope">
            <el-tag :type="getHoursTagType(scope.row.weeklyHours)">
              {{ scope.row.weeklyHours }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="classCount" label="授课班级" width="100"></el-table-column>
        <el-table-column prop="studentCount" label="学生人数" width="100"></el-table-column>
        <el-table-column prop="workloadLevel" label="工作负荷" width="120">
          <template slot-scope="scope">
            <el-tag :type="getWorkloadType(scope.row.workloadLevel)">
              {{ getWorkloadLabel(scope.row.workloadLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workloadPercentage" label="负荷率" width="120">
          <template slot-scope="scope">
            <el-progress :percentage="Math.min(scope.row.workloadPercentage, 100)" 
                        :color="getProgressColor(scope.row.workloadPercentage)">
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">
              <i class="el-icon-view"></i> 详情
            </el-button>
            <el-button type="text" size="small" @click="viewCourses(scope.row)">
              <i class="el-icon-document"></i> 课程
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination">
      </el-pagination>
    </el-card>

    <!-- 教师详情对话框 -->
    <el-dialog title="教师工作量详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentTeacher">
        <el-descriptions-item label="教师编号">{{ currentTeacher.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="教师姓名">{{ currentTeacher.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentTeacher.department }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentTeacher.title }}</el-descriptions-item>
        <el-descriptions-item label="总课时">{{ currentTeacher.totalHours }}小时</el-descriptions-item>
        <el-descriptions-item label="周课时">{{ currentTeacher.weeklyHours }}小时</el-descriptions-item>
        <el-descriptions-item label="授课班级">{{ currentTeacher.classCount }}个</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ currentTeacher.studentCount }}人</el-descriptions-item>
        <el-descriptions-item label="工作负荷">
          <el-tag :type="getWorkloadType(currentTeacher.workloadLevel)">
            {{ getWorkloadLabel(currentTeacher.workloadLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="负荷率">
          <el-progress :percentage="Math.min(currentTeacher.workloadPercentage, 100)" 
                      :color="getProgressColor(currentTeacher.workloadPercentage)">
          </el-progress>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 课程列表对话框 -->
    <el-dialog :title="currentTeacher ? currentTeacher.teacherName + ' - 授课列表' : '授课列表'" 
               :visible.sync="coursesVisible" width="900px">
      <el-table :data="currentCourses" v-if="currentTeacher">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="courseNo" label="课程编号" width="120"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="180"></el-table-column>
        <el-table-column prop="className" label="班级" width="150"></el-table-column>
        <el-table-column prop="weeklyHours" label="周学时" width="100"></el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="100"></el-table-column>
        <el-table-column prop="classTime" label="上课时间"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "TeacherWorkload",
  data() {
    return {
      loading: false,
      selectedSemester: '',
      searchKeyword: '',
      
      // 数据
      semesters: [],
      teachers: [],
      displayTeachers: [],
      
      // 分页
      page: 1,
      pageSize: 10,
      total: 0,
      
      // 对话框
      detailVisible: false,
      coursesVisible: false,
      currentTeacher: null,
      currentCourses: [],
      
      // 统计数据
      stats: {
        totalTeachers: 0,
        activeTeachers: 0,
        averageHours: 0,
        totalHours: 0,
        lowWorkload: 0,
        normalWorkload: 0,
        highWorkload: 0,
        overload: 0
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
      this.getTeacherWorkload();
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
    
    // 获取教师工作量数据
    getTeacherWorkload() {
      this.loading = true;
      
      const params = {};
      if (this.selectedSemester) {
        params.semester = this.selectedSemester;
      }
      
      this.$axios.get('http://localhost:8080/teacher/workload/all', { params })
        .then(response => {
          if (response.data.code === 200) {
            this.teachers = response.data.data || [];
            this.displayTeachers = this.teachers;
            this.total = this.teachers.length;
            this.applyFilters();
            this.getStatsSummary();
            
            // 绘制图表
            this.$nextTick(() => {
              this.drawCharts();
            });
          } else {
            this.$message.error(response.data.msg || '获取数据失败');
          }
        })
        .catch(error => {
          console.error('获取数据失败:', error);
          this.$message.error('获取数据失败');
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
      
      this.$axios.get('http://localhost:8080/teacher/workload/summary', { params })
        .then(response => {
          if (response.data.code === 200) {
            this.stats = response.data.data;
          }
        })
        .catch(error => {
          console.error('获取统计摘要失败:', error);
        });
    },
    
    // 应用筛选
    applyFilters() {
      let filtered = [...this.teachers];
      
      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(t => 
          t.teacherName.toLowerCase().includes(keyword) ||
          t.teacherNo.toLowerCase().includes(keyword)
        );
      }
      
      this.displayTeachers = filtered;
      this.total = filtered.length;
    },
    
    // 绘制图表
    drawCharts() {
      this.drawWorkloadDistChart();
      this.drawWorkloadPieChart();
    },
    
    // 绘制工作量分布图
    drawWorkloadDistChart() {
      const chartDom = document.getElementById('workloadDistChart');
      if (!chartDom) return;
      
      const myChart = echarts.init(chartDom);
      
      // 按周课时排序
      const sortedTeachers = [...this.teachers].sort((a, b) => b.weeklyHours - a.weeklyHours);
      const top20 = sortedTeachers.slice(0, 20);
      
      const option = {
        title: {
          text: '教师工作量排名（Top 20）',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '周课时'
        },
        yAxis: {
          type: 'category',
          data: top20.map(t => t.teacherName),
          inverse: true
        },
        series: [{
          name: '周课时',
          type: 'bar',
          data: top20.map(t => ({
            value: t.weeklyHours,
            itemStyle: {
              color: this.getBarColor(t.workloadLevel)
            }
          })),
          label: {
            show: true,
            position: 'right'
          }
        }]
      };
      
      myChart.setOption(option);
      window.addEventListener('resize', () => myChart.resize());
    },
    
    // 绘制工作负荷饼图
    drawWorkloadPieChart() {
      const chartDom = document.getElementById('workloadPieChart');
      if (!chartDom) return;
      
      const myChart = echarts.init(chartDom);
      
      const option = {
        title: {
          text: '工作负荷等级分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['轻负荷', '正常', '重负荷', '超负荷']
        },
        series: [{
          name: '教师数量',
          type: 'pie',
          radius: '60%',
          data: [
            { value: this.stats.lowWorkload, name: '轻负荷', itemStyle: { color: '#67C23A' } },
            { value: this.stats.normalWorkload, name: '正常', itemStyle: { color: '#409EFF' } },
            { value: this.stats.highWorkload, name: '重负荷', itemStyle: { color: '#E6A23C' } },
            { value: this.stats.overload, name: '超负荷', itemStyle: { color: '#F56C6C' } }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      };
      
      myChart.setOption(option);
      window.addEventListener('resize', () => myChart.resize());
    },
    
    // 获取柱状图颜色
    getBarColor(level) {
      const colors = {
        'low': '#67C23A',
        'normal': '#409EFF',
        'high': '#E6A23C',
        'overload': '#F56C6C'
      };
      return colors[level] || '#409EFF';
    },
    
    // 获取课时标签类型
    getHoursTagType(hours) {
      if (hours < 8) return 'success';
      if (hours <= 16) return 'primary';
      if (hours <= 20) return 'warning';
      return 'danger';
    },
    
    // 获取工作负荷类型
    getWorkloadType(level) {
      const types = {
        'low': 'success',
        'normal': 'primary',
        'high': 'warning',
        'overload': 'danger'
      };
      return types[level] || 'info';
    },
    
    // 获取工作负荷标签
    getWorkloadLabel(level) {
      const labels = {
        'low': '轻负荷',
        'normal': '正常',
        'high': '重负荷',
        'overload': '超负荷'
      };
      return labels[level] || '未知';
    },
    
    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage < 60) return '#67C23A';
      if (percentage <= 100) return '#409EFF';
      if (percentage <= 140) return '#E6A23C';
      return '#F56C6C';
    },
    
    // 刷新数据
    refreshData() {
      this.getTeacherWorkload();
      this.$message.success('数据已刷新');
    },
    
    // 导出报表
    exportReport() {
      try {
        let csvContent = '教师编号,教师姓名,所属部门,职称,总课时,周课时,授课班级,学生人数,工作负荷,负荷率(%)\n';
        
        this.displayTeachers.forEach(teacher => {
          const row = [
            teacher.teacherNo,
            teacher.teacherName,
            teacher.department,
            teacher.title,
            teacher.totalHours,
            teacher.weeklyHours,
            teacher.classCount,
            teacher.studentCount,
            this.getWorkloadLabel(teacher.workloadLevel),
            teacher.workloadPercentage
          ].join(',');
          csvContent += row + '\n';
        });
        
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `教师工作量统计_${this.selectedSemester || '全部'}_${new Date().toLocaleDateString()}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        this.$message.success('报表导出成功！');
      } catch (error) {
        this.$message.error('导出失败：' + error.message);
      }
    },
    
    // 学期变化
    handleSemesterChange() {
      this.getTeacherWorkload();
    },
    
    // 搜索
    handleSearch() {
      this.applyFilters();
    },
    
    // 分页
    handleSizeChange(val) {
      this.pageSize = val;
      this.page = 1;
    },
    
    handleCurrentChange(val) {
      this.page = val;
    },
    
    // 查看详情
    viewDetail(row) {
      this.currentTeacher = row;
      this.detailVisible = true;
    },
    
    // 查看课程
    viewCourses(row) {
      this.currentTeacher = row;
      this.currentCourses = row.courses || [];
      this.coursesVisible = true;
    }
  }
}
</script>

<style scoped lang="less">
.teacher-workload-page {
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
      color: #409EFF;
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

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  .card-header {
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
  }
  
  .chart {
    width: 100%;
    height: 400px;
  }
}

.table-card {
  .card-header {
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
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
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
  
  .charts-container {
    grid-template-columns: 1fr;
  }
}
</style>
