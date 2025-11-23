<template>
  <!-- 学生端主界面 -->
  <!-- 学生点击修改密码，跳转到updatepass.vue与其他用户共用一个组件 -->
  <!-- 学生登录之后默认进入查看课表页面 -->
  <!-- 点击学生右上角的个人中心跳转到一个页面展示个人信息，上方放一个按钮加入班级，一个修改信息 -->
  <div class="wrapper">
    <el-container>
      <el-header>
        <!-- 头 -->
        <el-header style="text-align: right; font-size: 12px">
          <!-- 系统标题 -->
          <el-dropdown @command="handleCommand">
            <i class="el-icon-setting" style="margin-right: 15px"></i>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="center">个人中心</el-dropdown-item>
              <el-dropdown-item command="exit">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>您好，
          <span>{{name}}</span>
        </el-header>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <!-- 侧边 -->
          <!-- 默认展开的索引default-active -->
          <el-menu :default-active="default_active" @select="handleSelect" unique-opened>
            <el-menu-item index="/courseList">
              <template slot="title">
                <i class="el-icon-s-marketing"></i>
                <span slot="title">课程表</span>
              </template>
            </el-menu-item>
             <el-menu-item index="/center">
              <template slot="title">
                <i class="el-icon-user"></i>
                <span slot="title">个人中心</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/password">
              <template slot="title">
                <i class="el-icon-unlock"></i>
                <span slot="title">修改密码</span>
              </template>
            </el-menu-item>
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
      name: "用户名"
    };
  },
  computed: {},
  mounted() {
    setInterval(() => {
      this.getTime();
    }, 1000);

    let student = window.localStorage.getItem("student")
    if (student != null) {
      this.name = JSON.parse(student).realname
    }
  },

  methods: {
    handleCommand(command) {
      // alert(command)
      if (command == "exit") {
        localStorage.removeItem("token");
        localStorage.removeItem("student");
        // 判断，返回指定页面
        this.$router.push('/');
      } else if (command == "center") {
        // 跳转到个人中心
        this.$router.push("/center");
      }
    },

    // 获取系统时间
    getTime() {
      this.time = new Date().toLocaleString();
    },

    // 展开一个菜单
    handleSelect(val) {
      this.default_active = val;
      if(val=='/index'){
        // 网课页面
        window.open('http://localhost:81/index.html')
        return;
      }
      this.$router.push(val);
    }
  }
};
</script>

<style lang="less" scoped>
.wrapper {
  height: 100%;
  width: 100%;
  .a {
    text-decoration: none !important;
    color: #303133 !important;
  }
  .links {
    text-decoration: none !important;
    color: #303133 !important;
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
  text-align: right;
  line-height: 60px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  
  span {
    color: #fff;
    font-weight: 600;
  }
  
  .el-icon-setting {
    cursor: pointer;
    font-size: 20px;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: rotate(90deg);
    }
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
  
  /deep/ .el-menu-item {
    color: #ecf0f1;
    transition: all 0.3s ease;
    
    &:hover {
      background-color: #34495e !important;
      color: #fff;
    }
    
    &.is-active {
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) !important;
      color: #fff;
    }
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