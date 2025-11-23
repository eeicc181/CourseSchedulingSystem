<template>
  <div class="classroom-utilization-page">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="el-icon-office-building"></i>
          教室利用率分析
        </h1>
        <p class="subtitle">实时监控教室使用情况，优化资源配置</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" icon="el-icon-refresh" @click="refreshData" :loading="loading">
          刷新数据
        </el-button>
        <el-button type="success" icon="el-icon-download" @click="exportReport">
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="toolbar">
      <el-select 
        v-model="selectedSemester" 
        placeholder="选择学期" 
        size="medium" 
        class="select-item" 
        @change="handleSemesterChange"
      >
        <el-option 
          v-for="item in semesters" 
          :key="item" 
          :value="item" 
          :label="item"
        ></el-option>
      </el-select>

      <el-select 
        v-model="selectedBuilding" 
        placeholder="选择教学楼" 
        size="medium" 
        class="select-item" 
        @change="handleBuildingChange" 
        clearable
      >
        <el-option label="全部教学楼" value=""></el-option>
        <el-option 
          v-for="item in buildings" 
          :key="item.id" 
          :value="item.id" 
          :label="item.name"
        ></el-option>
      </el-select>

      <el-input
        v-model="searchKeyword"
        placeholder="搜索教室名称"
        prefix-icon="el-icon-search"
        size="medium"
        class="search-input"
        @input="handleSearch"
        clearable
      />
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards" v-loading="loading">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalClassrooms }}</div>
            <div class="stat-label">教室总数</div>
            <div class="stat-trend">
              <span class="trend-info">可用 {{ stats.availableClassrooms }} 间</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-pie-chart"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.utilizationRate }}%</div>
            <div class="stat-label">平均利用率</div>
            <div class="stat-trend">
              <span :class="getTrendClass(stats.utilizationRate)">
                <i :class="getTrendIcon(stats.utilizationRate)"></i>
                {{ getTrendText(stats.utilizationRate) }}
              </span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.peakHours }}</div>
            <div class="stat-label">高峰时段</div>
            <div class="stat-trend">
              <span class="trend-info">利用率 {{ stats.peakUtilization }}%</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
            <i class="el-icon-s-data"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsageHours }}</div>
            <div class="stat-label">总使用时长(小时)</div>
            <div class="stat-trend">
              <span class="trend-info">本周统计</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 教室列表 -->
    <el-card class="table-card" shadow="hover">
      <div class="card-header">
        <h3><i class="el-icon-s-grid"></i> 教室详细信息</h3>
      </div>

      <el-table
        :data="displayClassrooms"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
      >
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        
        <el-table-column prop="name" label="教室名称" width="150" align="center">
          <template slot-scope="scope">
            <div class="classroom-name">
              <i class="el-icon-office-building"></i>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="building" label="所属教学楼" width="120" align="center"></el-table-column>

        <el-table-column prop="capacity" label="容量" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="info">{{ scope.row.capacity }}人</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="utilizationRate" label="利用率" width="150" align="center">
          <template slot-scope="scope">
            <el-progress
              :percentage="scope.row.utilizationRate"
              :color="getUtilizationColor(scope.row.utilizationRate)"
              :stroke-width="12"
            ></el-progress>
          </template>
        </el-table-column>

        <el-table-column prop="usedHours" label="已用时长" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.usedHours }}h</span>
          </template>
        </el-table-column>

        <el-table-column prop="availableHours" label="空闲时长" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.availableHours }}h</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="当前状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="使用情况" min-width="200">
          <template slot-scope="scope">
            <div class="usage-timeline">
              <el-tooltip
                v-for="(slot, index) in scope.row.timeSlots"
                :key="index"
                :content="slot.info"
                placement="top"
              >
                <div
                  class="time-slot"
                  :class="slot.status"
                  :style="{width: (100 / scope.row.timeSlots.length) + '%'}"
                ></div>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button 
              type="primary" 
              icon="el-icon-view" 
              size="mini" 
              @click="viewDetail(scope.row)" 
              circle
            ></el-button>
            <el-button 
              type="success" 
              icon="el-icon-time" 
              size="mini" 
              @click="viewSchedule(scope.row)" 
              circle
            ></el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 第一行：利用率图表和状态饼图 -->
      <div class="chart-row">
        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-s-data"></i> 教室利用率统计</h3>
            <el-radio-group v-model="chartType" size="small" @change="drawUtilizationChart">
              <el-radio-button label="bar">柱状图</el-radio-button>
              <el-radio-button label="line">折线图</el-radio-button>
            </el-radio-group>
          </div>
          <div id="utilizationChart" class="chart"></div>
        </el-card>

        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-pie-chart"></i> 教室使用状态分布</h3>
          </div>
          <div id="statusPieChart" class="chart"></div>
        </el-card>
      </div>

      <!-- 第二行：时段热力图和周趋势图 -->
      <div class="chart-row">
        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-data-analysis"></i> 时段使用热力图</h3>
          </div>
          <div id="heatmapChart" class="chart"></div>
        </el-card>

        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-trend-charts"></i> 周使用趋势</h3>
          </div>
          <div id="weekTrendChart" class="chart"></div>
        </el-card>
      </div>
    </div>

    <!-- 教室详情对话框 -->
    <el-dialog
      title="教室详细信息"
      :visible.sync="detailVisible"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentClassroom">
        <el-descriptions-item label="教室名称">{{ currentClassroom.name }}</el-descriptions-item>
        <el-descriptions-item label="所属教学楼">{{ currentClassroom.building }}</el-descriptions-item>
        <el-descriptions-item label="容量">{{ currentClassroom.capacity }}人</el-descriptions-item>
        <el-descriptions-item label="利用率">
          <el-progress
            :percentage="currentClassroom.utilizationRate"
            :color="getUtilizationColor(currentClassroom.utilizationRate)"
          ></el-progress>
        </el-descriptions-item>
        <el-descriptions-item label="已用时长">{{ currentClassroom.usedHours }}小时</el-descriptions-item>
        <el-descriptions-item label="空闲时长">{{ currentClassroom.availableHours }}小时</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusType(currentClassroom.status)">
            {{ getStatusLabel(currentClassroom.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备">{{ currentClassroom.equipment || '标准配置' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 课表查看对话框 -->
    <el-dialog
      :title="currentClassroom ? currentClassroom.name + ' - 本周课表' : '教室课表'"
      :visible.sync="scheduleVisible"
      width="900px"
    >
      <div class="schedule-container" v-if="currentClassroom">
        <div class="schedule-legend">
          <span class="legend-item">
            <span class="legend-color" style="background: #67C23A;"></span>
            <span>空闲</span>
          </span>
          <span class="legend-item">
            <span class="legend-color" style="background: #E6A23C;"></span>
            <span>已占用</span>
          </span>
        </div>
        
        <table class="schedule-table">
          <thead>
            <tr>
              <th class="time-header">时间</th>
              <th v-for="day in weekDays" :key="day">{{ day }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(period, index) in timePeriods" :key="index">
              <td class="time-cell">{{ period }}</td>
              <td 
                v-for="day in 7" 
                :key="day" 
                :class="getCellClass(index, day)"
                @click="handleCellClick(index, day)"
              >
                <div class="cell-content">
                  {{ getCellContent(index, day) }}
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ClassroomUtilization",
  data() {
    return {
      loading: false,
      selectedSemester: '',
      selectedBuilding: '',
      searchKeyword: '',
      chartType: 'bar', // 图表类型
      
      // 数据
      semesters: [],
      buildings: [],
      classrooms: [],
      displayClassrooms: [],
      
      // 分页
      page: 1,
      pageSize: 10,
      total: 0,
      
      // 对话框
      detailVisible: false,
      scheduleVisible: false,
      currentClassroom: null,
      
      // 统计数据
      stats: {
        totalClassrooms: 0,
        availableClassrooms: 0,
        utilizationRate: 0,
        peakHours: '--',
        peakUtilization: 0,
        totalUsageHours: 0
      },
      
      // 课表数据
      weekDays: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      timePeriods: ['第1-2节\n08:00-09:35', '第3-4节\n09:50-11:25', '第5-6节\n14:00-15:35', '第7-8节\n15:50-17:25', '第9-10节\n19:00-20:35'],
      scheduleData: {}
    };
  },
  
  mounted() {
    this.initData();
  },
  
  methods: {
    // 初始化数据
    initData() {
      this.getSemesters();
      this.getBuildings();
      this.getClassroomData();
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
          this.$message.error("获取学期列表失败");
        });
    },
    
    // 获取教学楼列表
    getBuildings() {
      this.$axios.get("http://localhost:8080/teachbuild/findAll")
        .then(res => {
          this.buildings = res.data.data;
        })
        .catch(error => {
          this.$message.error("获取教学楼列表失败");
        });
    },
    
    // 获取教室数据
    getClassroomData() {
      this.loading = true;
      
      // 调用真实API
      const params = {};
      if (this.selectedSemester) {
        params.semester = this.selectedSemester;
      }
      
      this.$axios.get('http://localhost:8080/classroom/utilization/all', { params })
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data || [];
            // 转换数据格式以匹配前端
            this.classrooms = data.map(item => ({
              id: item.id,
              name: item.classroomName,
              classroomNo: item.classroomNo,
              building: item.teachBuildName,
              teachbuildNo: item.teachbuildNo,
              capacity: item.capacity,
              utilizationRate: item.utilizationRate,
              usedHours: item.usedHours,
              availableHours: item.availableHours,
              status: item.status,
              timeSlots: item.timeSlots || [],
              equipment: item.equipment || '投影仪、音响',
              remark: item.remark
            }));
            
            this.displayClassrooms = this.classrooms;
            this.total = this.classrooms.length;
            this.calculateStats();
            this.applyFilters();
            
            // 绘制图表
            this.$nextTick(() => {
              this.drawCharts();
            });
          } else {
            this.$message.error(response.data.msg || '获取数据失败');
            // 失败时使用模拟数据
            this.generateMockData();
          }
        })
        .catch(error => {
          console.error('获取数据失败:', error);
          this.$message.error('获取数据失败，使用模拟数据');
          // 出错时使用模拟数据
          this.generateMockData();
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 生成模拟数据
    generateMockData() {
      this.classrooms = [];
      const buildings = ['A栋', 'B栋', 'C栋', 'D栋'];
      
      for (let i = 1; i <= 50; i++) {
        const building = buildings[Math.floor(Math.random() * buildings.length)];
        const utilizationRate = Math.floor(Math.random() * 100);
        const usedHours = Math.floor(utilizationRate * 0.35);
        const availableHours = 35 - usedHours;
        
        this.classrooms.push({
          id: i,
          name: `${building}${String(i).padStart(3, '0')}`,
          building: building,
          capacity: [30, 50, 80, 100, 150][Math.floor(Math.random() * 5)],
          utilizationRate: utilizationRate,
          usedHours: usedHours,
          availableHours: availableHours,
          status: this.getRandomStatus(),
          timeSlots: this.generateTimeSlots(usedHours, 35),
          equipment: '投影仪、音响'
        });
      }
      
      this.displayClassrooms = this.classrooms;
      this.total = this.classrooms.length;
      this.calculateStats();
      this.applyFilters();
      
      // 绘制图表
      this.$nextTick(() => {
        this.drawCharts();
      });
    },
    
    // 生成时间段数据
    generateTimeSlots(used, total) {
      const slots = [];
      for (let i = 0; i < 10; i++) {
        const isUsed = i < (used / total * 10);
        slots.push({
          status: isUsed ? 'used' : 'free',
          info: isUsed ? '已占用' : '空闲'
        });
      }
      return slots;
    },
    
    // 获取随机状态
    getRandomStatus() {
      const statuses = ['free', 'occupied', 'maintenance'];
      const weights = [0.6, 0.3, 0.1];
      const random = Math.random();
      let sum = 0;
      
      for (let i = 0; i < statuses.length; i++) {
        sum += weights[i];
        if (random < sum) return statuses[i];
      }
      
      return 'free';
    },
    
    // 计算统计数据
    calculateStats() {
      const total = this.classrooms.length;
      const available = this.classrooms.filter(c => c.status === 'free').length;
      const avgUtilization = Math.round(
        this.classrooms.reduce((sum, c) => sum + c.utilizationRate, 0) / total
      );
      const totalHours = this.classrooms.reduce((sum, c) => sum + c.usedHours, 0);
      
      this.stats = {
        totalClassrooms: total,
        availableClassrooms: available,
        utilizationRate: avgUtilization,
        peakHours: '第3-4节',
        peakUtilization: Math.min(avgUtilization + 15, 100),
        totalUsageHours: totalHours
      };
    },
    
    // 应用筛选
    applyFilters() {
      let filtered = [...this.classrooms];
      
      // 教学楼筛选
      if (this.selectedBuilding) {
        const building = this.buildings.find(b => b.id === this.selectedBuilding);
        if (building) {
          filtered = filtered.filter(c => c.building === building.name);
        }
      }
      
      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(c => 
          c.name.toLowerCase().includes(keyword)
        );
      }
      
      this.displayClassrooms = filtered;
      this.total = filtered.length;
    },
    
    // 刷新数据
    refreshData() {
      this.getClassroomData();
      this.$message.success('数据已刷新');
    },
    
    // 导出报表
    exportReport() {
      try {
        // 生成CSV内容
        let csvContent = '教室名称,所属教学楼,容量,利用率(%),已用时长(h),空闲时长(h),当前状态\n';
        
        this.displayClassrooms.forEach(classroom => {
          const row = [
            classroom.name,
            classroom.building,
            classroom.capacity,
            classroom.utilizationRate,
            classroom.usedHours,
            classroom.availableHours,
            this.getStatusLabel(classroom.status)
          ].join(',');
          csvContent += row + '\n';
        });
        
        // 创建Blob并下载
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `教室利用率报表_${this.selectedSemester || '全部'}_${new Date().toLocaleDateString()}.csv`);
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
      this.getClassroomData();
    },
    
    // 教学楼变化
    handleBuildingChange() {
      this.applyFilters();
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
      this.currentClassroom = row;
      this.detailVisible = true;
    },
    
    // 查看课表
    viewSchedule(row) {
      this.currentClassroom = row;
      this.generateScheduleData(row);
      this.scheduleVisible = true;
    },
    
    // 生成课表数据
    generateScheduleData(classroom) {
      // 生成模拟课表数据
      this.scheduleData = {};
      for (let day = 1; day <= 7; day++) {
        for (let period = 0; period < 5; period++) {
          const key = `${period}-${day}`;
          // 根据利用率随机生成占用情况
          const isOccupied = Math.random() * 100 < classroom.utilizationRate;
          this.scheduleData[key] = {
            occupied: isOccupied,
            course: isOccupied ? '课程' + Math.floor(Math.random() * 10) : ''
          };
        }
      }
    },
    
    // 获取单元格类名
    getCellClass(periodIndex, day) {
      const key = `${periodIndex}-${day}`;
      const data = this.scheduleData[key];
      if (!data) return 'cell-free';
      return data.occupied ? 'cell-occupied' : 'cell-free';
    },
    
    // 获取单元格内容
    getCellContent(periodIndex, day) {
      const key = `${periodIndex}-${day}`;
      const data = this.scheduleData[key];
      if (!data || !data.occupied) return '空闲';
      return data.course || '已占用';
    },
    
    // 单元格点击
    handleCellClick(periodIndex, day) {
      const key = `${periodIndex}-${day}`;
      const data = this.scheduleData[key];
      if (data && data.occupied) {
        this.$message.info(`${this.weekDays[day-1]} ${this.timePeriods[periodIndex].split('\n')[0]}: ${data.course}`);
      }
    },
    
    // 获取利用率颜色
    getUtilizationColor(rate) {
      if (rate < 30) return '#67C23A';
      if (rate < 60) return '#409EFF';
      if (rate < 80) return '#E6A23C';
      return '#F56C6C';
    },
    
    // 获取状态类型
    getStatusType(status) {
      const types = {
        free: 'success',
        occupied: 'warning',
        maintenance: 'danger'
      };
      return types[status] || 'info';
    },
    
    // 获取状态标签
    getStatusLabel(status) {
      const labels = {
        free: '空闲',
        occupied: '使用中',
        maintenance: '维护中'
      };
      return labels[status] || '未知';
    },
    
    // 获取趋势类名
    getTrendClass(rate) {
      if (rate >= 70) return 'trend-up';
      if (rate >= 40) return 'trend-normal';
      return 'trend-down';
    },
    
    // 获取趋势图标
    getTrendIcon(rate) {
      if (rate >= 70) return 'el-icon-top';
      if (rate >= 40) return 'el-icon-minus';
      return 'el-icon-bottom';
    },
    
    // 获取趋势文本
    getTrendText(rate) {
      if (rate >= 70) return '利用率良好';
      if (rate >= 40) return '利用率正常';
      return '利用率偏低';
    },
    
    // 绘制所有图表
    drawCharts() {
      this.drawUtilizationChart();
      this.drawStatusPieChart();
      this.drawHeatmapChart();
      this.drawWeekTrendChart();
    },
    
    // 绘制利用率图表
    drawUtilizationChart() {
      const chartDom = document.getElementById('utilizationChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      
      // 按教学楼分组统计
      const buildingStats = {};
      this.classrooms.forEach(c => {
        if (!buildingStats[c.building]) {
          buildingStats[c.building] = [];
        }
        buildingStats[c.building].push(c.utilizationRate);
      });
      
      const buildings = Object.keys(buildingStats);
      const avgRates = buildings.map(b => {
        const rates = buildingStats[b];
        return Math.round(rates.reduce((a, b) => a + b, 0) / rates.length);
      });
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: buildings,
          axisLabel: { fontSize: 12 }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: { 
            formatter: '{value}%',
            fontSize: 12
          }
        },
        series: [{
          name: '利用率',
          type: this.chartType,
          data: avgRates,
          itemStyle: {
            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ])
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%',
            fontSize: 11
          },
          smooth: true
        }]
      };
      
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },
    
    // 绘制状态饼图
    drawStatusPieChart() {
      const chartDom = document.getElementById('statusPieChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      
      const statusCount = {
        free: 0,
        occupied: 0,
        maintenance: 0
      };
      
      this.classrooms.forEach(c => {
        statusCount[c.status]++;
      });
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '10%',
          top: 'center',
          textStyle: { fontSize: 12 }
        },
        series: [{
          name: '教室状态',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          data: [
            { value: statusCount.free, name: '空闲', itemStyle: { color: '#67C23A' } },
            { value: statusCount.occupied, name: '使用中', itemStyle: { color: '#E6A23C' } },
            { value: statusCount.maintenance, name: '维护中', itemStyle: { color: '#F56C6C' } }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          label: { fontSize: 12 }
        }]
      };
      
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },
    
    // 绘制热力图
    drawHeatmapChart() {
      const chartDom = document.getElementById('heatmapChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      
      const hours = ['第1-2节', '第3-4节', '第5-6节', '第7-8节', '第9-10节'];
      const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      const data = [];
      
      for (let i = 0; i < days.length; i++) {
        for (let j = 0; j < hours.length; j++) {
          const value = Math.floor(Math.random() * 100);
          data.push([i, j, value]);
        }
      }
      
      const option = {
        tooltip: {
          position: 'top',
          formatter: function(params) {
            return days[params.data[0]] + ' ' + hours[params.data[1]] + '<br/>利用率: ' + params.data[2] + '%';
          }
        },
        grid: {
          left: '10%',
          right: '5%',
          top: '5%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: days,
          splitArea: { show: true },
          axisLabel: { fontSize: 11 }
        },
        yAxis: {
          type: 'category',
          data: hours,
          splitArea: { show: true },
          axisLabel: { fontSize: 11 }
        },
        visualMap: {
          min: 0,
          max: 100,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '0%',
          inRange: {
            color: ['#50a3ba', '#eac736', '#d94e5d']
          },
          textStyle: { fontSize: 11 }
        },
        series: [{
          name: '利用率',
          type: 'heatmap',
          data: data,
          label: { show: false },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      };
      
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },
    
    // 绘制周趋势图
    drawWeekTrendChart() {
      const chartDom = document.getElementById('weekTrendChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      
      const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      const data = days.map(() => Math.floor(Math.random() * 40) + 60);
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: days,
          boundaryGap: false,
          axisLabel: { fontSize: 12 }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            formatter: '{value}%',
            fontSize: 12
          }
        },
        series: [{
          name: '利用率',
          type: 'line',
          data: data,
          smooth: true,
          areaStyle: {
            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
            ])
          },
          itemStyle: {
            color: '#667eea'
          },
          lineStyle: {
            width: 3
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%',
            fontSize: 11
          }
        }]
      };
      
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    }
  }
};
</script>

<style lang="less" scoped>
.classroom-utilization-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  // 页面头部
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .header-content {
      h1 {
        margin: 0;
        font-size: 24px;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 10px;

        i {
          color: #667eea;
        }
      }

      .subtitle {
        margin: 5px 0 0 0;
        color: #909399;
        font-size: 14px;
      }
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  // 工具栏
  .toolbar {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;
    padding: 15px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .select-item {
      width: 180px;
    }

    .search-input {
      width: 250px;
    }
  }

  // 统计卡片
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 20px;

    .stat-card {
      border-radius: 12px;
      transition: all 0.3s ease;
      cursor: pointer;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 20px;
        padding: 10px;

        .stat-icon {
          width: 70px;
          height: 70px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;

          i {
            font-size: 32px;
            color: white;
          }
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #303133;
            line-height: 1.2;
            margin-bottom: 5px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 5px;
          }

          .stat-trend {
            font-size: 12px;

            .trend-info {
              color: #909399;
            }

            .trend-up {
              color: #67C23A;
            }

            .trend-normal {
              color: #409EFF;
            }

            .trend-down {
              color: #F56C6C;
            }
          }
        }
      }
    }
  }

  // 图表容器
  .charts-container {
    margin-bottom: 20px;

    .chart-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      margin-bottom: 20px;

      .chart-card {
        border-radius: 12px;

        .card-header {
          padding: 15px 20px;
          border-bottom: 1px solid #EBEEF5;
          display: flex;
          justify-content: space-between;
          align-items: center;

          h3 {
            margin: 0;
            font-size: 16px;
            color: #303133;
            display: flex;
            align-items: center;
            gap: 8px;

            i {
              color: #667eea;
            }
          }
        }

        .chart {
          width: 100%;
          height: 350px;
          padding: 10px;
        }
      }
    }
  }

  // 表格卡片
  .table-card {
    border-radius: 12px;

    .card-header {
      padding: 15px 20px;
      border-bottom: 1px solid #EBEEF5;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 8px;

        i {
          color: #667eea;
        }
      }
    }

    .classroom-name {
      display: flex;
      align-items: center;
      gap: 5px;

      i {
        color: #667eea;
      }
    }

    .usage-timeline {
      display: flex;
      gap: 2px;
      height: 20px;

      .time-slot {
        border-radius: 2px;
        transition: all 0.3s;

        &.used {
          background: #E6A23C;
        }

        &.free {
          background: #67C23A;
        }

        &:hover {
          opacity: 0.8;
          transform: scaleY(1.2);
        }
      }
    }

    .pagination-container {
      display: flex;
      justify-content: flex-end;
      padding: 20px;
    }
  }

  // 课表容器
  .schedule-container {
    .schedule-legend {
      display: flex;
      gap: 20px;
      margin-bottom: 15px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 8px;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;

        .legend-color {
          width: 20px;
          height: 20px;
          border-radius: 4px;
        }
      }
    }

    .schedule-table {
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;

      thead {
        background: #f5f7fa;

        th {
          padding: 12px 8px;
          text-align: center;
          font-weight: 600;
          color: #303133;
          border: 1px solid #EBEEF5;

          &.time-header {
            width: 120px;
            background: #667eea;
            color: white;
          }
        }
      }

      tbody {
        td {
          padding: 10px 8px;
          text-align: center;
          border: 1px solid #EBEEF5;
          transition: all 0.3s;
          cursor: pointer;

          &.time-cell {
            background: #f5f7fa;
            font-weight: 500;
            color: #606266;
            white-space: pre-line;
            font-size: 12px;
          }

          &.cell-free {
            background: #f0f9ff;
            color: #67C23A;

            &:hover {
              background: #e6f7ff;
            }
          }

          &.cell-occupied {
            background: #fff7e6;
            color: #E6A23C;
            font-weight: 500;

            &:hover {
              background: #ffe7ba;
              transform: scale(1.05);
            }
          }

          .cell-content {
            min-height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .classroom-utilization-page {
    padding: 10px;

    .page-header {
      flex-direction: column;
      gap: 15px;

      .header-actions {
        width: 100%;
        justify-content: center;
      }
    }

    .toolbar {
      flex-direction: column;

      .select-item,
      .search-input {
        width: 100%;
      }
    }

    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
    }

    .charts-container {
      .chart-row {
        grid-template-columns: 1fr;
        gap: 15px;

        .chart-card .chart {
          height: 300px;
        }
      }
    }
  }
}
</style>
