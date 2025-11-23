<template>
  <div class="class-table">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="value1" placeholder="选择学期" size="medium" class="select-item">
          <el-option
            v-for="item in semester"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <i class="el-icon-date"></i>
            <span>{{ item.label }}</span>
          </el-option>
        </el-select>
        
        <el-select v-model="value2" placeholder="选择年级" size="medium" class="select-item" @change="queryClass">
          <el-option
            v-for="item in grade"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <i class="el-icon-school"></i>
            <span>{{ item.label }}</span>
          </el-option>
        </el-select>
        
        <el-select v-model="value3" placeholder="选择班级" size="medium" class="select-item" @change="queryCoursePlan">
          <el-option
            v-for="item in classNo"
            :key="item.value"
            :label="item.lable"
            :value="item.value"
          >
            <i class="el-icon-user"></i>
            <span>{{ item.lable }}</span>
          </el-option>
        </el-select>
      </div>
      
      <div class="toolbar-right">
        <el-button type="primary" icon="el-icon-refresh" size="medium" @click="refreshTable" :loading="loading">刷新</el-button>
        <el-button type="success" icon="el-icon-printer" size="medium" @click="printTable">打印</el-button>
        <el-button type="info" icon="el-icon-download" size="medium" @click="exportTable">导出</el-button>
      </div>
    </div>

    <!-- 统计信息卡片 -->
    <div class="stats-cards" v-if="stats.totalCourses > 0">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-document stat-icon" style="color: #409EFF"></i>
          <div class="stat-text">
            <div class="stat-value">{{ stats.totalCourses }}</div>
            <div class="stat-label">总课程数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-user stat-icon" style="color: #67C23A"></i>
          <div class="stat-text">
            <div class="stat-value">{{ stats.totalTeachers }}</div>
            <div class="stat-label">授课教师</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-office-building stat-icon" style="color: #E6A23C"></i>
          <div class="stat-text">
            <div class="stat-value">{{ stats.totalClassrooms }}</div>
            <div class="stat-label">使用教室</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-time stat-icon" style="color: #F56C6C"></i>
          <div class="stat-text">
            <div class="stat-value">{{ stats.totalHours }}</div>
            <div class="stat-label">总学时</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 课表主体 -->
    <div class="table-wrapper" v-loading="loading">
      <div class="table-container" id="printArea">
        <div class="table-header">
          <h2>{{ currentClassName }} 课程表</h2>
          <p class="table-subtitle">{{ value1 }} 学期</p>
        </div>
        
        <table class="course-table">
          <thead>
            <tr>
              <th class="time-column">
                <div class="th-content">
                  <i class="el-icon-time"></i>
                  <span>时间</span>
                </div>
              </th>
              <th
                v-for="(weekNum, weekIndex) in classTableData.courses.length"
                :key="weekIndex"
                :class="{'weekend': weekIndex >= 5}"
              >
                <div class="th-content">
                  <div class="week-name">{{ "周" + digital2Chinese(weekIndex + 1, "week") }}</div>
                  <div class="week-date">{{ getWeekDate(weekIndex) }}</div>
                </div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(lesson, lessonIndex) in classTableData.lessons"
              :key="lessonIndex"
              :class="{'break-time': lessonIndex === 2}"
            >
              <td class="time-cell">
                <div class="time-content">
                  <div class="lesson-number">{{ "第" + digital2Chinese(lessonIndex + 1) + "节" }}</div>
                  <div class="time-range">{{ lesson }}</div>
                </div>
              </td>

              <td
                v-for="(course, courseIndex) in classTableData.courses"
                :key="courseIndex"
                :class="getCourseClass(courseIndex, lessonIndex)"
                @click="showCourseDetail(courseIndex, lessonIndex)"
              >
                <div class="course-cell" v-if="classTableData.courses[courseIndex][lessonIndex]">
                  <div class="course-info">
                    <div class="course-name">{{ getCourseName(classTableData.courses[courseIndex][lessonIndex]) }}</div>
                    <div class="teacher-name">{{ getTeacherName(classTableData.courses[courseIndex][lessonIndex]) }}</div>
                    <div class="classroom-name">{{ getClassroom(classTableData.courses[courseIndex][lessonIndex]) }}</div>
                  </div>
                  <div class="course-tag" :style="{backgroundColor: getCourseColor(classTableData.courses[courseIndex][lessonIndex])}"></div>
                </div>
                <div class="empty-cell" v-else>
                  <i class="el-icon-minus"></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog
      title="课程详情"
      :visible.sync="courseDetailVisible"
      width="500px"
      center
    >
      <div class="course-detail" v-if="currentCourse">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            <el-tag type="primary">{{ currentCourse.courseName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            <i class="el-icon-user"></i> {{ currentCourse.teacher }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            <i class="el-icon-location"></i> {{ currentCourse.classroom }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            <i class="el-icon-time"></i> {{ currentCourse.time }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      semester: [],
      grade: [
        { value: "01", label: "大一" },
        { value: "02", label: "大二" },
        { value: "03", label: "大三" },
        { value: "04", label: "大四" },
      ],
      classNo: [],
      value1: "",
      value2: "",
      value3: "",
      currentClassName: "",
      loading: false,
      courseDetailVisible: false,
      currentCourse: null,
      classTableData: {
        lessons: [
          "08:00-09:35",
          "09:50-11:25",
          "14:00-15:35",
          "15:50-17:25",
          "19:00-20:35",
        ],
        courses: [[], [], [], [], []],
      },
      stats: {
        totalCourses: 0,
        totalTeachers: 0,
        totalClassrooms: 0,
        totalHours: 0,
      },
      courseColors: {},
    };
  },
  created() {
    this.getSemester();
  },
  mounted() {},
  methods: {
    getSemester() {
      this.$axios
        .get("http://localhost:8080/semester")
        .then((res) => {
          res.data.data.forEach((element) => {
            this.semester.push({
              value: element,
              label: element,
            });
          });
        })
        .catch((error) => {});
    },

    // 查询班级编号，班级名
    queryClass() {
      this.loading = true;
      this.$axios
        .get("http://localhost:8080/class-grade/" + this.value2)
        .then((res) => {
          let r = res.data.data;
          this.classNo.splice(0, this.classNo.length);
          this.value3 = "";
          this.currentClassName = "";
          r.map((v) => {
            this.classNo.push({
              value: v.classNo,
              lable: v.className
            });
          });
        })
        .catch((error) => {
          this.$message.error("查询班级失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 查询课程表
    queryCoursePlan() {
      this.loading = true;
      // 清空课表数据
      this.classTableData.courses.map((item, index) => {
        this.classTableData.courses[index].splice(0, this.classTableData.courses[index].length);
      });
      
      // 设置当前班级名称
      const selectedClass = this.classNo.find(c => c.value === this.value3);
      this.currentClassName = selectedClass ? selectedClass.lable : "";
      
      this.$axios
        .get("http://localhost:8080/courseplan/" + this.value3)
        .then((res) => {
          let courseData = res.data.data;
          let level = 0;
          let times = 0;
          
          // 统计数据
          let teachers = new Set();
          let classrooms = new Set();
          
          for (let index = 0; index < courseData.length; index++) {
            times = times + 1;
            const item = courseData[index];
            
            if (parseInt(item.classTime) != times) {
              this.classTableData.courses[level].push("");
              index = index - 1;
            } else {
              const courseStr = item.teacher.realname + "-" + 
                               item.courseInfo.courseName + "(" + 
                               item.classroomNo + ")";
              this.classTableData.courses[level].push(courseStr);
              
              // 收集统计信息
              teachers.add(item.teacher.realname);
              classrooms.add(item.classroomNo);
              
              // 为每个课程分配颜色
              if (!this.courseColors[item.courseInfo.courseName]) {
                this.courseColors[item.courseInfo.courseName] = this.generateCourseColor();
              }
            }
            
            if (times % 5 == 0) {
              level = level + 1;
            }
          }
          
          // 更新统计信息
          this.stats.totalCourses = courseData.length;
          this.stats.totalTeachers = teachers.size;
          this.stats.totalClassrooms = classrooms.size;
          this.stats.totalHours = courseData.length * 2; // 假设每节课2学时
          
          this.$message({ message: "课表加载成功", type: "success" });
        })
        .catch((error) => {
          this.$message.error("加载课表失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 刷新课表
    refreshTable() {
      if (this.value3) {
        this.queryCoursePlan();
      } else {
        this.$message.warning("请先选择班级");
      }
    },
    
    // 打印课表
    printTable() {
      if (!this.value3) {
        this.$message.warning("请先选择班级");
        return;
      }
      window.print();
    },
    
    // 导出课表
    exportTable() {
      if (!this.value3) {
        this.$message.warning("请先选择班级");
        return;
      }
      this.$message.info("导出功能开发中...");
    },
    
    // 显示课程详情
    showCourseDetail(courseIndex, lessonIndex) {
      const courseStr = this.classTableData.courses[courseIndex][lessonIndex];
      if (!courseStr) return;
      
      const teacher = this.getTeacherName(courseStr);
      const courseName = this.getCourseName(courseStr);
      const classroom = this.getClassroom(courseStr);
      const weekDay = this.digital2Chinese(courseIndex + 1, "week");
      const lessonNum = this.digital2Chinese(lessonIndex + 1);
      
      this.currentCourse = {
        teacher,
        courseName,
        classroom,
        time: `周${weekDay} 第${lessonNum}节 ${this.classTableData.lessons[lessonIndex]}`
      };
      
      this.courseDetailVisible = true;
    },
    
    // 获取课程样式类
    getCourseClass(courseIndex, lessonIndex) {
      const course = this.classTableData.courses[courseIndex][lessonIndex];
      return {
        'has-course': !!course,
        'empty-course': !course,
        'weekend-course': courseIndex >= 5
      };
    },
    
    // 解析课程信息
    getTeacherName(courseStr) {
      if (!courseStr) return '';
      return courseStr.split('-')[0] || '';
    },
    
    getCourseName(courseStr) {
      if (!courseStr) return '';
      const match = courseStr.match(/-(.*?)\(/);
      return match ? match[1] : '';
    },
    
    getClassroom(courseStr) {
      if (!courseStr) return '';
      const match = courseStr.match(/\((.*?)\)/);
      return match ? match[1] : '';
    },
    
    // 生成课程颜色
    generateCourseColor() {
      const colors = [
        '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', 
        '#909399', '#00d4ff', '#5daf34', '#ff9800',
        '#9c27b0', '#ff5722', '#795548', '#607d8b'
      ];
      return colors[Object.keys(this.courseColors).length % colors.length];
    },
    
    // 获取课程颜色
    getCourseColor(courseStr) {
      const courseName = this.getCourseName(courseStr);
      return this.courseColors[courseName] || '#409EFF';
    },
    
    // 获取周几的日期
    getWeekDate(weekIndex) {
      const today = new Date();
      const day = today.getDay();
      const diff = weekIndex - (day === 0 ? 6 : day - 1);
      const targetDate = new Date(today.getTime() + diff * 24 * 60 * 60 * 1000);
      return `${targetDate.getMonth() + 1}/${targetDate.getDate()}`;
    },

    /**
     * 数字转中文
     * @param {Number} num 需要转换的数字
     * @param {String} identifier 标识符
     * @returns {String} 转换后的中文
     */
    digital2Chinese(num, identifier) {
      const character = [
        "零",
        "一",
        "二",
        "三",
        "四",
        "五",
        // "六",
        // "七",
        // "八",
      ];
      return identifier === "week" && (num === 0 || num === 7)
        ? "日"
        : character[num];
    },
  },
};
</script>

<style lang="less" scoped>
.class-table {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
  
  // 工具栏样式
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
    
    .toolbar-left {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
      
      .select-item {
        min-width: 180px;
      }
    }
    
    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }
  
  // 统计卡片样式
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
    
    .stat-card {
      border-radius: 8px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
      }
      
      .stat-content {
        display: flex;
        align-items: center;
        gap: 15px;
        
        .stat-icon {
          font-size: 36px;
          opacity: 0.8;
        }
        
        .stat-text {
          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
            line-height: 1.2;
          }
          
          .stat-label {
            font-size: 14px;
            color: #909399;
            margin-top: 5px;
          }
        }
      }
    }
  }
  
  // 课表容器
  .table-wrapper {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
  }
  
  .table-container {
    padding: 30px;
    overflow-x: auto;
    
    .table-header {
      text-align: center;
      margin-bottom: 30px;
      
      h2 {
        font-size: 28px;
        color: #303133;
        margin: 0 0 10px 0;
        font-weight: 600;
      }
      
      .table-subtitle {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }
    
    .course-table {
      width: 100%;
      border-collapse: separate;
      border-spacing: 0;
      min-width: 800px;
      
      thead {
        th {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          padding: 15px 10px;
          font-weight: 500;
          font-size: 14px;
          border: none;
          position: sticky;
          top: 0;
          z-index: 10;
          
          &.time-column {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }
          
          &.weekend {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
          }
          
          &:first-child {
            border-top-left-radius: 8px;
          }
          
          &:last-child {
            border-top-right-radius: 8px;
          }
          
          .th-content {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 5px;
            
            i {
              font-size: 16px;
            }
            
            .week-name {
              font-size: 15px;
              font-weight: 600;
            }
            
            .week-date {
              font-size: 12px;
              opacity: 0.9;
            }
          }
        }
      }
      
      tbody {
        tr {
          transition: all 0.3s ease;
          
          &:hover {
            background: #f5f7fa;
          }
          
          &.break-time {
            background: #fff9e6;
          }
          
          .time-cell {
            background: #fafafa;
            border-right: 2px solid #e4e7ed;
            font-weight: 500;
            
            .time-content {
              display: flex;
              flex-direction: column;
              gap: 5px;
              padding: 10px;
              
              .lesson-number {
                font-size: 14px;
                color: #303133;
                font-weight: 600;
              }
              
              .time-range {
                font-size: 12px;
                color: #909399;
              }
            }
          }
          
          td {
            padding: 8px;
            border: 1px solid #ebeef5;
            position: relative;
            min-height: 80px;
            vertical-align: middle;
            
            &.has-course {
              cursor: pointer;
              transition: all 0.3s ease;
              
              &:hover {
                transform: scale(1.05);
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                z-index: 5;
              }
            }
            
            &.empty-course {
              background: #fafafa;
              
              .empty-cell {
                color: #dcdfe6;
                font-size: 20px;
              }
            }
            
            &.weekend-course {
              background: #fff9f0;
            }
            
            .course-cell {
              position: relative;
              padding: 12px 8px;
              border-radius: 6px;
              background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
              min-height: 70px;
              display: flex;
              flex-direction: column;
              justify-content: center;
              
              .course-info {
                .course-name {
                  font-size: 14px;
                  font-weight: 600;
                  color: #303133;
                  margin-bottom: 6px;
                  line-height: 1.4;
                }
                
                .teacher-name {
                  font-size: 12px;
                  color: #606266;
                  margin-bottom: 4px;
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  
                  &:before {
                    content: '👨‍🏫';
                    font-size: 12px;
                  }
                }
                
                .classroom-name {
                  font-size: 11px;
                  color: #909399;
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  
                  &:before {
                    content: '📍';
                    font-size: 11px;
                  }
                }
              }
              
              .course-tag {
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                width: 4px;
                border-radius: 6px 0 0 6px;
              }
            }
          }
        }
      }
    }
  }
  
  // 课程详情对话框
  .course-detail {
    padding: 10px 0;
  }
}

// 打印样式
@media print {
  .toolbar,
  .stats-cards {
    display: none !important;
  }
  
  .table-container {
    box-shadow: none !important;
  }
  
  .course-table {
    thead th {
      background: #667eea !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .class-table {
    padding: 10px;
    
    .toolbar {
      flex-direction: column;
      gap: 15px;
      
      .toolbar-left,
      .toolbar-right {
        width: 100%;
        justify-content: center;
      }
    }
    
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .table-container {
      padding: 15px;
      
      .table-header h2 {
        font-size: 20px;
      }
    }
  }
}
</style>