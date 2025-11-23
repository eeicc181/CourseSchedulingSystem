<template>
  <div class="login-wrapper">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-container">
      <div class="login-box">
        <!-- 头部标题 -->
        <div class="login-header">
          <div class="login-avatar">
            <img src="@/assets/logo.png" alt="Logo" />
          </div>
          <h2 class="login-title">智能排课系统</h2>
          <p class="login-subtitle">学生登录入口</p>
        </div>

        <!-- 登录表单 -->
        <el-form class="login-form" ref="loginFormRef" :model="studentLoginForm" :rules="studentLoginFormRules">
          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input 
              v-model="studentLoginForm.username" 
              placeholder="请输入学号" 
              prefix-icon="el-icon-user"
              size="large"
              clearable>
            </el-input>
          </el-form-item>
          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input 
              v-model="studentLoginForm.password" 
              placeholder="请输入密码" 
              @keyup.enter.native="login" 
              prefix-icon="el-icon-lock" 
              type="password"
              size="large"
              show-password>
            </el-input>
          </el-form-item>        
          <!-- 按钮 -->
          <el-form-item class="button-group">
            <el-button type="primary" @click="login" class="login-btn" size="large">登 录</el-button>
            <el-button type="text" @click="registerNo" class="register-link">还没有账号？立即注册</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 页脚信息 -->
        <div class="login-footer">
          <p> 2024 智能排课管理系统</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      // 登录表单的对象
      studentLoginForm: {
        username: '2020011234',
        password: ''
      },
      studentLoginFormRules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 12, message: '长度在 5 到 12 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur' }
        ]
      },
    }
  },
  methods: {
    
    registerNo() {
      // 跳转到注册页面
      window.location.href="http://localhost:8081/#/student/register"
    },
    
    login() {
      // 表单预验证
      this.$refs.loginFormRef.validate(valid => {
        if (!valid) return;
        this.$axios.post('http://localhost:8080/student/login', {
        username: this.studentLoginForm.username,
        password: this.studentLoginForm.password
        })
        .then((res) => {
          if (res.data.code == 0) {
            // 成功响应,得到token
            let ret = res.data.data
            window.localStorage.setItem('token', ret.token)
            window.localStorage.setItem('student', JSON.stringify(ret.student))
            this.$router.push('/student')
            this.$message({message: "登录成功", type: "success"})
          } else {
            alert(res.data.message)
          }
        }).catch((error) => {
          // 失败
          this.$message.error("登录失败")
        });
      })
    }
  }
};
</script>

<style lang="less" scoped>
.login-wrapper {
  position: relative;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

// 背景装饰动画
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 20s infinite ease-in-out;
  }
  
  .circle-1 {
    width: 300px;
    height: 300px;
    top: -150px;
    left: -150px;
    animation-delay: 0s;
  }
  
  .circle-2 {
    width: 200px;
    height: 200px;
    bottom: -100px;
    right: -100px;
    animation-delay: 5s;
  }
  
  .circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    right: 10%;
    animation-delay: 10s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 450px;
  padding: 20px;
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-avatar {
  width: 90px;
  height: 90px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 3px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.05) rotate(5deg);
  }
  
  img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background-color: #fff;
    object-fit: cover;
  }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.login-form {
  margin-top: 20px;
  
  /deep/ .el-form-item {
    margin-bottom: 22px;
  }
  
  /deep/ .el-input__inner {
    border-radius: 10px;
    border: 2px solid #e8e8e8;
    padding: 12px 15px 12px 40px;
    transition: all 0.3s ease;
    
    &:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
  }
  
  /deep/ .el-input__prefix {
    left: 12px;
    color: #999;
  }
}

.button-group {
  margin-top: 30px;
  
  .login-btn {
    width: 100%;
    height: 48px;
    border-radius: 10px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 1px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  .register-link {
    width: 100%;
    margin-top: 15px;
    color: #667eea;
    font-size: 14px;
    
    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }
  }
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  
  p {
    font-size: 12px;
    color: #999;
    margin: 0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    max-width: 90%;
  }
  
  .login-box {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 24px;
  }
}
</style>