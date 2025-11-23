<template>
  <div class="class-task-page">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select
          v-model="semester"
          placeholder="选择学期"
          size="medium"
          class="select-item"
          @change="handleSelectChange"
          clearable
        >
          <el-option
            v-for="(item, index) in semesterData"
            :key="index"
            :value="item"
            :label="item"
          >
            <i class="el-icon-date"></i>
            <span>{{ item }}</span>
          </el-option>
        </el-select>

        <el-select
          v-model="filterGrade"
          placeholder="筛选年级"
          size="medium"
          class="select-item"
          @change="handleFilter"
          clearable
        >
          <el-option
            v-for="item in grades"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <i class="el-icon-school"></i>
            <span>{{ item.label }}</span>
          </el-option>
        </el-select>

        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名或教师名"
          size="medium"
          class="search-input"
          prefix-icon="el-icon-search"
          @input="handleSearch"
          clearable
        />
      </div>

      <div class="toolbar-right">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="medium"
          @click="addClassTask()"
        >
          添加任务
        </el-button>
        <el-button
          type="success"
          icon="el-icon-s-operation"
          size="medium"
          @click="arrangeCourse()"
          :loading="arranging"
        >
          开始排课
        </el-button>
        <el-button
          type="warning"
          icon="el-icon-delete"
          size="medium"
          @click="batchDelete"
          :disabled="selectedTasks.length === 0"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总任务数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.teachers }}</div>
            <div class="stat-label">授课教师</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.courses }}</div>
            <div class="stat-label">课程数量</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalHours }}</div>
            <div class="stat-label">总学时</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        ref="taskTable"
        :data="displayData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="semester" label="学期" width="120" align="center">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ scope.row.semester }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="gradeNo" label="年级" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getGradeType(scope.row.gradeNo)" size="small">
              {{ getGradeLabel(scope.row.gradeNo) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="className" label="班级" width="120" align="center"></el-table-column>
        <el-table-column prop="courseNo" label="课号" width="100" align="center"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="150">
          <template slot-scope="scope">
            <div class="course-info">
              <i class="el-icon-reading"></i>
              <span>{{ scope.row.courseName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="courseAttr" label="课程属性" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getCourseAttrType(scope.row.courseAttr)" size="small">
              {{ getCourseAttrLabel(scope.row.courseAttr) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="realname" label="授课教师" width="100" align="center">
          <template slot-scope="scope">
            <div class="teacher-info">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.realname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="studentNum" label="学生人数" width="90" align="center"></el-table-column>

        <el-table-column prop="weeksNumber" label="周学时" width="80" align="center">
          <template slot-scope="scope">
            <el-tag type="warning" size="small">{{ scope.row.weeksNumber }}h</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="weeksSum" label="周数" width="70" align="center"></el-table-column>

        <el-table-column prop="isFix" label="固定时间" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isFix == '2' ? 'success' : 'info'" size="small">
              {{ scope.row.isFix == '2' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="classTime" label="时间" width="100" align="center"></el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="mini"
              @click="editById(scope.$index, scope.row)"
              circle
            ></el-button>
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="deleteById(scope.$index, scope.row)"
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
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 弹出表单添加课程计划 -->
    <el-dialog title="添加课程计划" :visible.sync="visible">
      <el-form :model="addClassTaskForm" label-position="left" label-width="80px" :rules="addClassTaskRules">

        <!-- 学期 -->
        <el-form-item label="学期" prop="semester">
          <el-input v-model="addClassTaskForm.semester" 
          autocomplete="off" 
          style="width: 200px;float: left;"></el-input>
          <span class="span">(输入格式如:2020-2021-1)</span>
        </el-form-item>

        <!-- 年级 -->
        <el-form-item label="年级" prop="gradeNo">
          <el-select v-model="addClassTaskForm.gradeNo" placeholder="请选择年级" style="width: 200px;float: left;" clearable>
            <el-option
              v-for="item in grades"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              @click.native="getGradeNo()"
              >
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-row>
          <el-col :span="8">
            <!-- 班级编号 -->
            <el-form-item label="班级" prop="className">
              <!-- <el-input v-model="addClassTaskForm.classNo" autocomplete="off"></el-input> -->
              <el-select v-model="addClassTaskForm.className" placeholder="请选择班级" style="width: 200px;float: left;" clearable>
                <el-option
                  v-for="item in classNames"
                  :key="item.value"
                  :label="item.label"
                  :value="item.label"
                  @click.native="choiceclassNo()"
                  >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 班级编号编号，根据选择的班级自动填充班级编号 -->
            <el-form-item label="班级编号" prop="classNo" style="width: 200px; margin-left: 60px">
              <el-input v-model="addClassTaskForm.classNo" autocomplete="off" :disabled="true" style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
          
        <el-row>
          <el-col :span="8">
            <!-- 课程名 -->
            <el-form-item label="课程名" prop="courseName">
              <el-select v-model="addClassTaskForm.courseName" placeholder="请选择课程" style="width: 200px;float: left;" clearable>
                <el-option
                  v-for="item in courseNames"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                  @click.native="choiceCourseNo()">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 课程编号，根据选择的课程名自动填充课程编号 -->
            <el-form-item label="课程编号" prop="courseNo" style="width: 200px; margin-left: 60px">
              <el-input v-model="addClassTaskForm.courseNo" autocomplete="off" :disabled="true" style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="8">
            <el-form-item label="讲师名字" prop="realname">
              <!-- <el-input v-model="addClassTaskForm.realname" autocomplete="off"></el-input> -->
              <el-select v-model="addClassTaskForm.realname" placeholder="请选教师" style="width: 200px;float: left;" clearable>
                <el-option
                  v-for="item in teacherNames"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                  @click.native="choiceRealNo()"
                  >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 教师编号，根据选择的教师名自动填充教师编号 -->
            <el-form-item label="讲师编号" prop="teacherNo" style="width: 200px; margin-left: 60px">
              <el-input v-model="addClassTaskForm.teacherNo" autocomplete="off" :disabled="true" style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 课程属性 -->
        <el-form-item label="课程属性" prop="courseAttr">
          <!-- <el-input v-model="addClassTaskForm.courseAttr" autocomplete="off"></el-input> -->
          <el-select v-model="addClassTaskForm.courseAttr" placeholder="课程属性" style="width: 200px;float: left;" clearable>
            <el-option
              v-for="item in courseAttrs"
              :key="item.id"
              :label="item.label"
              :value="item.value"
              >
            </el-option>
          </el-select>
        </el-form-item>
            
        <el-form-item label="学生人数" prop="studentNum">
          <el-input v-model="addClassTaskForm.studentNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="周学时" prop="weeksNumber">
          <el-input v-model="addClassTaskForm.weeksNumber" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="周数" prop="weeksSum">
          <el-input v-model="addClassTaskForm.weeksSum" autocomplete="off"></el-input>
        </el-form-item>

        <el-row>
          <el-col :span="8">
            <!-- 上课时间是否固定？ -->
            <el-form-item label="固定时间" prop="isFix">
              <!-- <el-input v-model="addClassTaskForm.isFix" autocomplete="off"></el-input> -->
              <el-select v-model="addClassTaskForm.isFix" placeholder="课程属性" style="width: 200px;float: left;" clearable>
                <el-option
                  v-for="item in isFixs"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                  >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 上课时间 -->
            <el-form-item label="上课时间" prop="classTime" style="width: 200px; margin-left: 60px">
              <el-input v-model="addClassTaskForm.classTime" 
              autocomplete="off" 
              :disabled="addClassTaskForm.isFix == '1'" 
              style="width: 200px;"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
            
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="commit()">提 交</el-button>
      </div>
    </el-dialog>

    <!-- 分页 -->
    <div class="footer-button">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="page"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: "ClassTaskList",
  data() {
    return {
      // 筛选和搜索
      filterGrade: '',
      searchKeyword: '',
      displayData: [],
      selectedTasks: [],
      arranging: false,
      
      classNames: [], // 班级编号数据
      courseNames: [], //课程名数据
      teacherNames: [], //教师数据
      importBtnDisabled: false, // 按钮是否禁用,
      loading: false,
      
      // 统计数据
      stats: {
        total: 0,
        teachers: 0,
        courses: 0,
        totalHours: 0
      },
      isFixs: [{
          value: '1',
          label: '否'
      },{
          value: '2',
          label: '是'
      }],
      courseAttrs: [{
          value: '01',
          label: '必修课程' 
      },{
          value: '02',
          label: '选修课程' 
      },{
          value: '03',
          label: '实验实践课程' 
      },{
          value: '04',
          label: '体育课程' 
      },{
          value: '05',
          label: '艺术课程' 
      }],
      grades: [{
          value: '01',
          label: '大一'
        }, {
          value: '02',
          label: '大二'
        }, {
          value: '03',
          label: '大三'
        }, {
          value: '04',
          label: '大四'
        }],
      classTaskData: [],
      semesterData: [],
      addClassTaskForm: {
        semester: '',
        gradeNo: '',
        className: '',
        classNo: '',
        courseNo: '',
        courseName: '',
        teacherNo: '',
        realname: '',
        courseAttr: '',
        studentNum: '',
        weeksNumber: '',
        weeksSum: '',
        isFix: '',
        classTime: ''
      },
      visible: false,
      updateOrAdd: '',
      page: 1,
      total: 0,
      pageSize: 10,
      // 学期选择默认值
      semester: "",
      fileList: [],
      addClassTaskRules: {
        semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
        gradeNo: [{ required: true, message: '请输入年级编号', trigger: 'blur' }],
        className: [{ required: true, message: '请选择班级', trigger: 'blur' }],
        classNo: [{ required: true, message: '请输入班级编号', trigger: 'blur' }],
        courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        teacherNo: [{ required: true, message: '请输入讲师编号', trigger: 'blur' }],
        realname: [{ required: true, message: '请输入讲师姓名', trigger: 'blur' }],
        courseAttr: [{ required: true, message: '请输入课程属性', trigger: 'blur' }],
        studentNum: [{ required: true, message: '请输入班级学生人数', trigger: 'blur' }],
        weeksNumber: [{ required: true, message: '请输入周学时', trigger: 'blur' }],
        weeksSum: [{ required: true, message: '请输入上课周数', trigger: 'blur' }],
        isFix: [{ required: true, message: '是否固定上课时间', trigger: 'blur' }],
        classTime: []
      }
    };
  },

  /**
   * 加载Vue实例时执行
   */
  mounted() {
    this.getSemester();
    this.allClassTask();
  },
  
  watch: {
    classTaskData() {
      this.calculateStats();
      this.applyFilters();
    }
  },
  created() {
      // 为是否固定上课时间选项设置默认值。
      this.addClassTaskForm.isFix = this.isFixs[0].value;
    },
  methods: {
    // 计算统计数据
    calculateStats() {
      const teachers = new Set();
      const courses = new Set();
      let totalHours = 0;
      
      this.classTaskData.forEach(task => {
        if (task.realname) teachers.add(task.realname);
        if (task.courseName) courses.add(task.courseName);
        totalHours += ((task.weeksNumber || 0) * (task.weeksSum || 0));
      });
      
      this.stats = {
        total: this.classTaskData.length,
        teachers: teachers.size,
        courses: courses.size,
        totalHours
      };
    },
    
    // 应用筛选
    applyFilters() {
      let filtered = [...this.classTaskData];
      
      // 年级筛选
      if (this.filterGrade) {
        filtered = filtered.filter(task => task.gradeNo === this.filterGrade);
      }
      
      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(task => 
          (task.courseName && task.courseName.toLowerCase().includes(keyword)) ||
          (task.realname && task.realname.toLowerCase().includes(keyword))
        );
      }
      
      this.displayData = filtered;
    },
    
    // 筛选处理
    handleFilter() {
      this.applyFilters();
    },
    
    // 搜索处理
    handleSearch() {
      this.applyFilters();
    },
    
    // 选择变化
    handleSelectionChange(selection) {
      this.selectedTasks = selection;
    },
    
    // 批量删除
    batchDelete() {
      if (this.selectedTasks.length === 0) {
        this.$message.warning('请先选择要删除的任务');
        return;
      }
      
      this.$confirm(`确定要删除选中的 ${this.selectedTasks.length} 个任务吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const deletePromises = this.selectedTasks.map(task => 
          this.$axios.delete(`http://localhost:8080/deleteclasstask/${task.id}`)
        );
        
        Promise.all(deletePromises)
          .then(() => {
            this.$message.success('批量删除成功');
            this.allClassTask();
          })
          .catch(() => {
            this.$message.error('批量删除失败');
          });
      }).catch(() => {});
    },
    
    // 获取年级标签
    getGradeLabel(gradeNo) {
      const gradeMap = {
        '01': '大一',
        '02': '大二',
        '03': '大三',
        '04': '大四'
      };
      return gradeMap[gradeNo] || gradeNo;
    },
    
    // 获取年级类型
    getGradeType(gradeNo) {
      const typeMap = {
        '01': 'success',
        '02': 'primary',
        '03': 'warning',
        '04': 'danger'
      };
      return typeMap[gradeNo] || '';
    },
    
    // 获取课程属性标签
    getCourseAttrLabel(attr) {
      const attrMap = {
        '01': '必修',
        '02': '选修',
        '03': '实践',
        '04': '体育',
        '05': '艺术'
      };
      return attrMap[attr] || attr;
    },
    
    // 获取课程属性类型
    getCourseAttrType(attr) {
      const typeMap = {
        '01': 'danger',
        '02': 'primary',
        '03': 'warning',
        '04': 'success',
        '05': 'info'
      };
      return typeMap[attr] || '';
    },
   
    // 获取所有教师名
    getRealnameAll(){
      this.teacherNames = [];
      this.$axios.get("http://localhost:8080/teacher/selectTeacherAll")
      .then(res => {
        res.data.data.forEach(val => {
          var lab = { label: val.realname, value: val.realname, key: val.id};
          this.teacherNames.push(lab);
        });
      })
      .catch(error => {
        alert(res.data.message);
      })
    },

    // 根据已选择的教师名填充教师编号
    choiceRealNo(){
      this.$axios.post("http://localhost:8080/teacher/selectTeacherNo", this.addClassTaskForm.realname)
      .then(res => {
        this.addClassTaskForm.teacherNo = res.data.data.teacherNo;
      })
      .catch(error => {
        alert(res.data.message);
      })
    },

    // 根据已选择的班级填充课班级编号
    choiceclassNo(){
      this.$axios.post("http://localhost:8080/classInfo/selectByClassName", this.addClassTaskForm.className)
      .then(res => {
        this.addClassTaskForm.classNo = res.data.data.classNo;
      })
      .catch(error => {
        alert(res.data.message)
      })
    },

    // 根据已选择的课程名填充课程编号
    choiceCourseNo(){
      this.$axios.post("http://localhost:8080/courseinfo/selectByCourseName", this.addClassTaskForm.courseName)
      .then(res => {
        this.addClassTaskForm.courseNo = res.data.data.courseNo;
      })
      .catch(error => {
        alert(res.data.message)
      })
    },
    
    // 获取所有课程名
    getCourseNameAll(){
      this.courseNames = [];
      this.$axios.get("http://localhost:8080/courseinfo/getCourse")
      .then(res => {
        res.data.data.forEach(val => {
          var lab = { label: val.courseName, value: val.courseName, key: val.id};
          this.courseNames.push(lab);
        });
      })
      .catch(error => {
        alert(res.data.message)
      })
    },

    // 获取所有课堂编号
    getGradeNo() {
      this.classNames = [];
      this.$axios.get("http://localhost:8080/class-grade/" + this.addClassTaskForm.gradeNo)
      .then(res => {
        res.data.data.forEach(val => {
          var lab = { label: val.className, value: val.classNo, key: val.id };
          this.classNames.push(lab);
        });
      })
      .catch(error => {
        alert('获取失败')
      })
    },

    // 提交添加
    commit() {
      if (this.updateOrAdd == '0'){
        this.$axios.post("http://localhost:8080/addclasstask", this.addClassTaskForm)
        .then(res => {
          if (res.data.code == 0) {
            // 添加完成
            this.allClassTask()
            this.visible = false
            this.$message({message: "添加课程任务成功！", type: "success"})
          } else {
            alert(res.data.message)
          }
        })
        .catch(error => {
        })
      } else if(this.updateOrAdd == '1'){
        this.$axios.post("http://localhost:8080/updateClasstask", this.addClassTaskForm)
        .then(res => {
          if (res.data.code == 0) {
            // 修改完成
            this.allClassTask()
            this.visible = false
            this.$message({message: "修改课程任务成功！", type: "success"})
          } else {
            alert(res.data.message)
          }
        })
        .catch(error => {
        })
      }
      
    },

    // 手动添加课程任务
    addClassTask() {
      this.updateOrAdd = '0';
      this.addClassTaskForm = {};
      this.getCourseNameAll();
      this.getRealnameAll();
      this.visible = true
    },

    // 点击开始提交学期到系统后台排课
    arrangeCourse() {
      if(!this.semester){
        this.$message.warning("请先选择学期");
        return;
      }
      
      this.$confirm('确定要开始排课吗？这可能需要一些时间。', '提示', {
        confirmButtonText: '开始排课',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.arranging = true;
        this.$axios.post("http://localhost:8080/arrange/" + this.semester)
          .then(res => {
            if (res.data.code == 0) {
              this.$message.success('排课成功！');
              this.allClassTask();
              this.$router.push('/coursetable');
            } else {
              this.$message.error(res.data.message || '排课失败');
            }
          })
          .catch(error => {
            this.$message.error('排课失败，请稍后重试');
          })
          .finally(() => {
            this.arranging = false;
          });
      }).catch(() => {});
    },

    // 上传成功
    uploadSuccess(response, file, fileList) {
      this.loading = false
      this.allClassTask()
      this.$message({message: "上传成功", type:"success"})
    },

    handleRemove(file, fileList) {

    },

    handleError(error, file, fileList) {
      alert("文件上传失败" + error)
    },

    // 提交上传文件事件
    // submitUpload() {
    //   this.loading = true
    //   this.$refs.upload.submit()
    // },

    // 得到对应选中的年级
    handleSelectChange(val) {
      // 这里的V就是选择的学期了
      this.semester = val
      
    },

    deleteById(index, row) {
      this.$confirm('确定要删除这个课程任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteClassTaskById(row.id);
      }).catch(() => {});
    },

    editById(index, row) {
      this.updateOrAdd = '1';
      this.addClassTaskForm = Object.assign({}, row);
      this.getCourseNameAll();
      this.getRealnameAll();
      this.getGradeNo();
      this.visible = true
    },

    handleSizeChange(val) {
      this.pageSize = val;
      this.page = 1;
      this.allClassTask();
    },

    handleCurrentChange(v) {
      this.page = v
      this.allClassTask()
    },

    /**
     * 获得学期列表后默认填充第一个到学期选择下拉列表中
     * 这里只放一个学期先吧
     */
    getSemester() {
      this.$axios
        .get("http://localhost:8080/semester")
        .then(res => {
          let ret = res.data.data
          this.semesterData = ret
        })
        .catch(error => {
        });
    },

    /**
     * 获得所有开课任务
     */
    allClassTask() {
      this.loading = true;
      let semesterBak = this.semester || "0";
      
      this.$axios
        .get(`http://localhost:8080/classtask/${this.page}/${semesterBak}`)
        .then(res => {
          let ret = res.data.data;
          this.classTaskData = ret.records;
          this.displayData = ret.records;
          this.total = ret.total;
        })
        .catch(error => {
          this.$message.error("查询开课任务失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },

    /**
     * 删除开课任务
     */
    deleteClassTaskById(id) {
      this.$axios
        .delete("http://localhost:8080/deleteclasstask/" + id)
        .then(res => {
          this.allClassTask();
          this.$message({ message: "删除成功", type: "success" })
        })
        .catch(error => {
          this.$message.error("删除失败")
        });
    }
  }
};
</script>

<style lang="less" scoped>
.class-task-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  // 工具栏
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 15px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .toolbar-left {
      display: flex;
      gap: 15px;
      flex: 1;

      .select-item {
        width: 180px;
      }

      .search-input {
        width: 250px;
      }
    }

    .toolbar-right {
      display: flex;
      gap: 10px;
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
          }
        }
      }
    }
  }

  // 表格卡片
  .table-card {
    border-radius: 12px;

    .course-info,
    .teacher-info {
      display: flex;
      align-items: center;
      gap: 5px;

      i {
        color: #667eea;
      }
    }

    .pagination-container {
      display: flex;
      justify-content: flex-end;
      padding: 20px 0;
    }
  }

  // 对话框提示
  .span {
    color: #E6A23C;
    margin-left: 10px;
    font-size: 12px;
  }

  // 底部分页（旧版兼容）
  .footer-button {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .class-task-page {
    padding: 10px;

    .toolbar {
      flex-direction: column;
      gap: 15px;

      .toolbar-left {
        flex-direction: column;
        width: 100%;

        .select-item,
        .search-input {
          width: 100%;
        }
      }

      .toolbar-right {
        width: 100%;
        justify-content: center;
      }
    }

    .stats-cards {
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
  }
}
</style>