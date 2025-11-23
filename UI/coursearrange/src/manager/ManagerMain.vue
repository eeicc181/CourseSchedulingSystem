<template>
  <!-- 后台管理系统主界面 -->
  <div class="wrapper">
    <el-container>
      <el-header>
        <!-- 头 -->
        <div class="header-left">
          <i class="el-icon-school" style="font-size: 28px; margin-right: 10px;"></i>
          <span class="system-title">高校智能排课管理系统</span>
        </div>
        <!-- 系统标题 -->
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <i class="el-icon-user-solid" style="margin-right: 8px;"></i>
              <span>{{name}}</span>
              <i class="el-icon-arrow-down" style="margin-left: 8px;"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="center">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="updatePassword">
                <i class="el-icon-lock"></i> 修改密码
              </el-dropdown-item>
              <el-dropdown-item command="exit" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <!-- 侧边 -->
          <!-- 默认展开的索引default-active -->
          <el-menu :default-active="default_active" @select="handleSelect" unique-opened>
            <el-menu-item index="0">
              <template slot="title">
                <router-link to="/systemdata" class="links">
                  <i class="el-icon-setting"></i>系统数据
                </router-link>
              </template>
            </el-menu-item>

            <el-submenu index="1">
              <template slot="title">
                <i class="el-icon-s-data"></i>排课管理
              </template>
              <el-menu-item index="1-1" v-if="!isTeacher">
                <router-link to="/classtasklist" class="links">课程计划</router-link>
              </el-menu-item>
              <el-menu-item index="1-2">
                <router-link to="/coursetable" class="links">查看课表</router-link>
              </el-menu-item>
            </el-submenu>

            <el-submenu index="2">
              <template slot="title">
                <i class="el-icon-reading"></i>基本管理
              </template>
                <el-menu-item index="2-1" v-if="!isTeacher">
                  <router-link to="/teacherlist" class="links">教师管理</router-link>
                </el-menu-item>
                <el-menu-item index="2-2">
                  <router-link to="/studentlist" class="links">学生管理</router-link>
                </el-menu-item>
                <el-menu-item index="2-3">
                  <router-link to="/classmanager" class="links">班级管理</router-link>
                </el-menu-item>
                <el-menu-item index="2-4">
                  <router-link to="/courseinfolist" class="links">课程管理</router-link>
                </el-menu-item>
            </el-submenu>
            <el-submenu index="3" v-if="!isTeacher">
              <template slot="title">
                <i class="el-icon-office-building"></i>教学设施
              </template>
              <el-menu-item index="3-1">
                <router-link class="links" to="/teachbuildinglist">教学楼管理</router-link>
              </el-menu-item>
              <el-menu-item index="3-2">
                <router-link to="/classroomlist" class="links">教室列表</router-link>
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>

        <el-main>
          <!-- Main区域，数据显示 -->
          <router-view></router-view>
        </el-main>

      </el-container>
      <!-- 显示系统时间 -->
      <el-footer>{{time}}</el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "ManagerMain",
  data() {
    return {
      time: "",
      default_active: "0",
      name: '用户名',
    };
  },
  computed: {
    isTeacher:()=>{
      return window.localStorage.getItem('teacher') != null;
    }
  },
  mounted() {
    setInterval(() => {
      this.getTime();
    }, 1000);
    
    let admin = window.localStorage.getItem('admin')
    if(admin != null){
      this.name = (JSON.parse(admin)).realname
    } else {
      let teacher = window.localStorage.getItem('teacher')
      if (teacher != null) {
        this.name = (JSON.parse(teacher)).realname
      }
    }
  },

  methods: {
    // 下拉菜单功能，退出、个人中心
    handleCommand(command) {
      // alert(command)
      if (command == 'exit') {
        localStorage.removeItem('token')
        localStorage.removeItem('admin')
        localStorage.removeItem('teacher')
        // 判断，返回指定页面
        this.$router.push('/')
      } else if (command == 'center') {
        // 跳转到个人中心
      } else if (command == 'updatePassword') {
        // 修改密码页面
        this.$router.push('/updatepass')
      }
      
    },

    // 获取系统时间
    getTime() {
      this.time = new Date().toLocaleString();
    },

    // 展开一个菜单
    handleSelect(val) {
      this.default_active = val;
    }
  }
};
</script>

<style lang="less" scoped>
.wrapper {
  height: 100%;
  width: 100%;
  
  .links {
    text-decoration: none !important;
    color: #ecf0f1 !important;
    display: block;
    width: 100%;
  }
}

.header-left {
  float: left;
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  
  .system-title {
    letter-spacing: 2px;
  }
}

.header-right {
  float: right;
  
  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 8px 16px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

.el-container {
  height: 100%;
  padding: 0;
  margin: 0;
  width: 100%;
}

.el-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  text-align: center;
  line-height: 60px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  span {
    color: #fff;
    font-weight: 600;
  }
}

.el-footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  text-align: center;
  line-height: 60px;
  font-size: 14px;
}

.el-aside {
  background: #2c3e50;
  color: #fff;
  text-align: center;
  line-height: 200px;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  
  /deep/ .el-menu {
    background-color: #2c3e50;
    border-right: none;
  }
  
  /deep/ .el-menu-item,
  /deep/ .el-submenu__title {
    color: #ecf0f1;
    transition: all 0.3s ease;
    
    &:hover {
      background-color: #34495e !important;
      color: #fff;
    }
  }
  
  /deep/ .el-menu-item.is-active {
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) !important;
    color: #fff;
  }
  
  /deep/ .el-submenu.is-opened > .el-submenu__title {
    background-color: #34495e;
  }
}

.el-main {
  background-color: #f5f7fa;
  color: #333;
  text-align: center;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

body > .el-container {
  margin-bottom: 40px;
}
.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}
</style>