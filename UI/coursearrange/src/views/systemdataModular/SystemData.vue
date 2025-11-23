<template>
  <div class="system-data-page">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="el-icon-data-analysis"></i>
          系统数据仪表盘
        </h1>
        <p class="subtitle">实时监控系统运行状态和数据统计</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" icon="el-icon-refresh" @click="refreshData" :loading="loading">刷新数据</el-button>
        <el-button type="success" icon="el-icon-download">导出报表</el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="stats-grid" v-loading="loading">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.teachers || 0 }}</div>
            <div class="stat-label">讲师总数</div>
            <div class="stat-trend">
              <span class="trend-up"><i class="el-icon-top"></i> +{{ sysData.teacherReg || 0 }} 本月新增</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.students || 0 }}</div>
            <div class="stat-label">学生总数</div>
            <div class="stat-trend">
              <span class="trend-up"><i class="el-icon-top"></i> +{{ sysData.studentReg || 0 }} 本月新增</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-school"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.classes || 0 }}</div>
            <div class="stat-label">班级总数</div>
            <div class="stat-trend">
              <span class="trend-stable"><i class="el-icon-minus"></i> 稳定</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.courses || 0 }}</div>
            <div class="stat-label">课程总数</div>
            <div class="stat-trend">
              <span class="trend-stable"><i class="el-icon-minus"></i> 稳定</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.classrooms || 0 }}</div>
            <div class="stat-label">教室总数</div>
            <div class="stat-trend">
              <span class="trend-stable"><i class="el-icon-minus"></i> 稳定</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);">
            <i class="el-icon-s-home"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.teachbuilds || 0 }}</div>
            <div class="stat-label">教学楼总数</div>
            <div class="stat-trend">
              <span class="trend-stable"><i class="el-icon-minus"></i> 稳定</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%);">
            <i class="el-icon-s-data"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ sysData.classtasks || 0 }}</div>
            <div class="stat-label">排课任务</div>
            <div class="stat-trend">
              <span class="trend-stable"><i class="el-icon-minus"></i> 稳定</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fdcbf1 0%, #e6dee9 100%);">
            <i class="el-icon-pie-chart"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ completionRate }}%</div>
            <div class="stat-label">排课完成率</div>
            <div class="stat-trend">
              <span class="trend-up"><i class="el-icon-top"></i> 进展良好</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 第一行：柱状图和饼图 -->
      <div class="chart-row">
        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-s-data"></i> 系统数据统计</h3>
            <el-radio-group v-model="chartType" size="small">
              <el-radio-button label="bar">柱状图</el-radio-button>
              <el-radio-button label="line">折线图</el-radio-button>
            </el-radio-group>
          </div>
          <div id="mainChart" class="chart"></div>
        </el-card>

        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-pie-chart"></i> 数据分布</h3>
          </div>
          <div id="pieChart" class="chart"></div>
        </el-card>
      </div>

      <!-- 第二行：雷达图和趋势图 -->
      <div class="chart-row">
        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-data-analysis"></i> 系统能力雷达图</h3>
          </div>
          <div id="radarChart" class="chart"></div>
        </el-card>

        <el-card class="chart-card" shadow="hover">
          <div class="card-header">
            <h3><i class="el-icon-trend-charts"></i> 月度增长趋势</h3>
          </div>
          <div id="trendChart" class="chart"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SystemData",
  data() {
    return {
      sysData: {},
      loading: false,
      chartType: 'bar'
    };
  },

  mounted() {
    this.systemData();
  },
  
  computed: {
    completionRate() {
      if (!this.sysData.classtasks || this.sysData.classtasks === 0) return 0;
      return Math.round((this.sysData.classtasks / (this.sysData.classes || 1)) * 100);
    }
  },
  
  watch: {
    chartType() {
      this.drawMainChart();
    }
  },
  
  methods: {
    // 刷新数据
    refreshData() {
      this.systemData();
    },

    // 获取系统数据
    systemData() {
      this.loading = true;
      this.$axios
        .get("http://localhost:8080/systemdata")
        .then(res => {
          if (res.data.code == 0) {
            this.sysData = res.data.data;
            this.$nextTick(() => {
              this.drawMainChart();
              this.drawPieChart();
              this.drawRadarChart();
              this.drawTrendChart();
            });
            this.$message.success('数据加载成功');
          } else {
            this.$message.error(res.data.message);
          }
        })
        .catch(error => {
          this.$message.error('数据加载失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 主图表（柱状图/折线图）
    drawMainChart() {
      const chartDom = document.getElementById('mainChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: '#333',
          textStyle: { color: '#fff' }
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
          data: ['讲师', '学生', '班级', '教室', '教学楼', '课程', '排课任务'],
          axisLabel: {
            interval: 0,
            rotate: 30,
            textStyle: { fontSize: 12 }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: { formatter: '{value}' }
        },
        series: [
          {
            name: '数量',
            type: this.chartType,
            data: [
              this.sysData.teachers || 0,
              this.sysData.students || 0,
              this.sysData.classes || 0,
              this.sysData.classrooms || 0,
              this.sysData.teachbuilds || 0,
              this.sysData.courses || 0,
              this.sysData.classtasks || 0
            ],
            itemStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ])
            },
            label: {
              show: true,
              position: 'top',
              textStyle: { fontSize: 12 }
            },
            smooth: true
          }
        ]
      };
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },

    // 饼图
    drawPieChart() {
      const chartDom = document.getElementById('pieChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '10%',
          top: 'center'
        },
        series: [
          {
            name: '数据分布',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: { show: false },
            data: [
              { value: this.sysData.teachers || 0, name: '讲师', itemStyle: { color: '#667eea' } },
              { value: this.sysData.students || 0, name: '学生', itemStyle: { color: '#764ba2' } },
              { value: this.sysData.classes || 0, name: '班级', itemStyle: { color: '#f093fb' } },
              { value: this.sysData.classrooms || 0, name: '教室', itemStyle: { color: '#fa709a' } },
              { value: this.sysData.courses || 0, name: '课程', itemStyle: { color: '#30cfd0' } }
            ]
          }
        ]
      };
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },

    // 雷达图
    drawRadarChart() {
      const chartDom = document.getElementById('radarChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      const maxValue = Math.max(
        this.sysData.teachers || 0,
        this.sysData.students || 0,
        this.sysData.classes || 0,
        this.sysData.classrooms || 0,
        this.sysData.courses || 0
      );
      
      const option = {
        tooltip: {},
        radar: {
          indicator: [
            { name: '讲师', max: maxValue },
            { name: '学生', max: maxValue },
            { name: '班级', max: maxValue },
            { name: '教室', max: maxValue },
            { name: '课程', max: maxValue }
          ],
          shape: 'polygon',
          splitNumber: 5,
          name: {
            textStyle: {
              color: '#333',
              fontSize: 14
            }
          },
          splitLine: {
            lineStyle: {
              color: [
                'rgba(102, 126, 234, 0.1)',
                'rgba(102, 126, 234, 0.2)',
                'rgba(102, 126, 234, 0.3)',
                'rgba(102, 126, 234, 0.4)',
                'rgba(102, 126, 234, 0.5)'
              ].reverse()
            }
          },
          splitArea: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(102, 126, 234, 0.5)'
            }
          }
        },
        series: [
          {
            name: '系统数据',
            type: 'radar',
            data: [
              {
                value: [
                  this.sysData.teachers || 0,
                  this.sysData.students || 0,
                  this.sysData.classes || 0,
                  this.sysData.classrooms || 0,
                  this.sysData.courses || 0
                ],
                name: '当前数据',
                areaStyle: {
                  color: new this.$echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                    { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
                    { offset: 1, color: 'rgba(118, 75, 162, 0.1)' }
                  ])
                },
                lineStyle: {
                  color: '#667eea',
                  width: 2
                },
                itemStyle: {
                  color: '#667eea'
                }
              }
            ]
          }
        ]
      };
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },

    // 趋势图
    drawTrendChart() {
      const chartDom = document.getElementById('trendChart');
      if (!chartDom) return;
      
      const chart = this.$echarts.init(chartDom);
      const months = ['一月', '二月', '三月', '四月', '五月', '六月'];
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['新增学生', '新增讲师'],
          top: '5%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: months
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '新增学生',
            type: 'line',
            smooth: true,
            data: this.generateTrendData(this.sysData.studentReg || 0),
            areaStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
              ])
            },
            lineStyle: { color: '#667eea', width: 3 },
            itemStyle: { color: '#667eea' }
          },
          {
            name: '新增讲师',
            type: 'line',
            smooth: true,
            data: this.generateTrendData(this.sysData.teacherReg || 0),
            areaStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(240, 147, 251, 0.5)' },
                { offset: 1, color: 'rgba(240, 147, 251, 0.1)' }
              ])
            },
            lineStyle: { color: '#f093fb', width: 3 },
            itemStyle: { color: '#f093fb' }
          }
        ]
      };
      chart.setOption(option);
      window.addEventListener('resize', () => chart.resize());
    },

    // 生成趋势数据（模拟数据）
    generateTrendData(currentValue) {
      const data = [];
      for (let i = 0; i < 6; i++) {
        data.push(Math.floor(currentValue * (0.5 + Math.random() * 0.5)));
      }
      return data;
    }
  }
};

</script>

<style lang="less" scoped>
.system-data-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  // 页面标题
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    margin-bottom: 30px;
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);

    .header-content {
      color: white;

      h1 {
        font-size: 28px;
        margin: 0 0 10px 0;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 10px;

        i {
          font-size: 32px;
        }
      }

      .subtitle {
        margin: 0;
        font-size: 14px;
        opacity: 0.9;
      }
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  // 统计卡片网格
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 30px;

    .stat-card {
      border-radius: 12px;
      transition: all 0.3s ease;
      cursor: pointer;

      &:hover {
        transform: translateY(-8px);
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
            margin-bottom: 8px;
          }

          .stat-trend {
            font-size: 12px;

            .trend-up {
              color: #67C23A;
              display: flex;
              align-items: center;
              gap: 4px;
            }

            .trend-down {
              color: #F56C6C;
              display: flex;
              align-items: center;
              gap: 4px;
            }

            .trend-stable {
              color: #909399;
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }

  // 图表容器
  .charts-container {
    .chart-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
      gap: 20px;
      margin-bottom: 20px;

      .chart-card {
        border-radius: 12px;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 20px 20px 10px 20px;
          border-bottom: 1px solid #ebeef5;

          h3 {
            margin: 0;
            font-size: 18px;
            color: #303133;
            display: flex;
            align-items: center;
            gap: 8px;

            i {
              font-size: 20px;
              color: #667eea;
            }
          }
        }

        .chart {
          width: 100%;
          height: 400px;
          padding: 20px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .system-data-page {
    .stats-grid {
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    }

    .charts-container .chart-row {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 768px) {
  .system-data-page {
    padding: 10px;

    .page-header {
      flex-direction: column;
      gap: 20px;
      padding: 20px;

      .header-content h1 {
        font-size: 22px;
      }

      .header-actions {
        width: 100%;
        justify-content: center;
      }
    }

    .stats-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;

      .stat-card .stat-content {
        flex-direction: column;
        text-align: center;

        .stat-icon {
          width: 60px;
          height: 60px;

          i {
            font-size: 28px;
          }
        }

        .stat-info .stat-value {
          font-size: 24px;
        }
      }
    }

    .charts-container .chart-row .chart-card .chart {
      height: 300px;
    }
  }
}
</style>