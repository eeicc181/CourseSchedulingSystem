# 🎓 高校智能排课管理系统

<div align="center">

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.2.4-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-2.5.2-brightgreen.svg)

基于遗传算法的智能排课系统 - 高效、智能、易用

[功能特性](#功能特性) • [技术栈](#技术栈) • [快速开始](#快速开始) • [项目结构](#项目结构) • [使用说明](#使用说明)

</div>

---

## 📖 项目简介

高校智能排课管理系统是一个基于遗传算法的自动排课解决方案,旨在解决传统手工排课效率低、易出错、难以优化等问题。系统采用前后端分离架构,提供友好的用户界面和强大的排课算法。

### ✨ 核心亮点

- 🧬 **智能算法**: 基于遗传算法的自动排课,快速生成优化课表
- 🎨 **现代UI**: 采用渐变色设计,流畅的动画效果,优秀的用户体验
- 🔐 **安全可靠**: JWT身份认证,数据加密传输
- 📱 **响应式设计**: 支持多设备访问,自适应不同屏幕尺寸
- ⚡ **高性能**: 优化的数据库查询,缓存机制,快速响应

---

## 🚀 功能特性

### 管理员功能
- ✅ 系统数据统计与可视化
- ✅ 自动排课与手动调整
- ✅ 教师信息管理
- ✅ 学生信息管理
- ✅ 班级信息管理
- ✅ 课程信息管理
- ✅ 教学楼与教室管理
- ✅ 空教室查询
- ✅ 课表查询与导出

### 教师功能
- ✅ 查看个人课表
- ✅ 管理班级学生
- ✅ 查看教学任务
- ✅ 修改个人信息

### 学生功能
- ✅ 查看个人课表
- ✅ 查看班级信息
- ✅ 修改个人信息
- ✅ 密码修改

---

## 🛠 技术栈

### 后端技术
- **框架**: Spring Boot 2.2.4
- **持久层**: MyBatis-Plus 3.2.0
- **数据库**: MySQL 8.0
- **身份认证**: JWT (java-jwt 3.8.3)
- **API文档**: Swagger 2.7.0
- **工具类**: Lombok, Apache Commons, Joda-Time

### 前端技术
- **框架**: Vue.js 2.5.2
- **UI组件**: Element UI 2.13.0
- **HTTP客户端**: Axios 0.19.2
- **图表库**: ECharts 4.7.0
- **状态管理**: Vuex 3.1.3
- **路由**: Vue Router 3.0.1
- **构建工具**: Webpack 3.6.0

### 开发工具
- **IDE**: IntelliJ IDEA / VS Code
- **版本控制**: Git
- **包管理**: Maven / npm
- **Java版本**: JDK 1.8

---

## 📦 快速开始

### 环境要求
- JDK 1.8+
- Node.js 6.0+
- MySQL 8.0+
- Maven 3.0+

### 后端启动

1. **克隆项目**
```bash
git clone <repository-url>
cd CourseArrange
```

2. **配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE db_course_arrangement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p db_course_arrangement < db_course_arrangement.sql
```

3. **修改配置文件**
```yaml
# src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_course_arrangement?useUnicode=true&characterEncoding=utf8
    username: your_username
    password: your_password
```

4. **启动后端服务**
```bash
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **进入前端目录**
```bash
cd UI/coursearrange
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

前端服务将在 `http://localhost:8081` 启动

4. **构建生产版本**
```bash
npm run build
```

---

## 📁 项目结构

```
CourseArrange/
├── src/                          # 后端源码
│   ├── main/
│   │   ├── java/
│   │   │   └── com/chq/coursearrange/
│   │   │       ├── controller/   # 控制器层
│   │   │       ├── service/      # 服务层
│   │   │       ├── dao/          # 数据访问层
│   │   │       ├── entity/       # 实体类
│   │   │       ├── vo/           # 视图对象
│   │   │       ├── config/       # 配置类
│   │   │       └── utils/        # 工具类
│   │   └── resources/
│   │       ├── mapper/           # MyBatis映射文件
│   │       └── application.yml   # 配置文件
│   └── test/                     # 测试代码
├── UI/                           # 前端源码
│   └── coursearrange/
│       ├── src/
│       │   ├── assets/           # 静态资源
│       │   ├── components/       # 组件
│       │   ├── views/            # 页面视图
│       │   ├── router/           # 路由配置
│       │   ├── home/             # 学生端
│       │   ├── manager/          # 管理端
│       │   └── pages/            # 登录注册页
│       ├── build/                # 构建配置
│       ├── config/               # 项目配置
│       └── package.json          # 依赖配置
├── docs/                         # 文档
│   ├── 技术总结文档.md
│   └── 项目优化清单.md
├── logs/                         # 日志文件
├── db_course_arrangement.sql     # 数据库脚本
├── pom.xml                       # Maven配置
└── README.md                     # 项目说明
```

---

## 📚 使用说明

### 默认账号

#### 管理员
- 账号: `admin`
- 密码: `123456`

#### 教师
- 账号: `teacher01`
- 密码: `123456`

#### 学生
- 账号: `2020011234`
- 密码: `123456`

### 排课流程

1. **准备基础数据**
   - 添加教师信息
   - 添加学生信息
   - 创建班级
   - 添加课程信息
   - 配置教学楼和教室

2. **创建开课任务**
   - 选择学期
   - 指定课程
   - 分配教师
   - 选择班级

3. **执行自动排课**
   - 点击"自动排课"按钮
   - 系统运行遗传算法
   - 生成优化课表

4. **查看和调整**
   - 查看生成的课表
   - 手动调整冲突
   - 导出课表

---

## 🔧 配置说明

### 后端配置

#### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db_course_arrangement
    username: root
    password: your_password
    type: com.alibaba.druid.pool.DruidDataSource
```

#### JWT配置
```java
// TokenService.java
private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
private static final String TOKEN_SECRET = "your_secret_key";
```

### 前端配置

#### API地址配置
```javascript
// src/main.js
axios.defaults.baseURL = 'http://localhost:8080'
```

#### 代理配置
```javascript
// config/index.js
proxyTable: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

---

## 🎨 界面预览

### 登录页面
- 现代渐变色背景
- 流畅的动画效果
- 响应式设计

### 管理后台
- 深色侧边栏
- 渐变色主题
- 卡片式布局

### 课表展示
- 清晰的表格展示
- 多维度筛选
- 一键导出

---

## 🔐 安全性

- ✅ JWT Token身份验证
- ✅ 密码加密存储 (建议使用BCrypt)
- ✅ SQL注入防护 (MyBatis预编译)
- ✅ XSS攻击防护
- ✅ CORS跨域配置

---

## 📈 性能优化

### 已实现
- ✅ 数据库连接池 (Druid)
- ✅ 分页查询
- ✅ 前端路由懒加载
- ✅ 静态资源压缩

### 建议优化
- 🔄 Redis缓存
- 🔄 数据库索引优化
- 🔄 异步任务处理
- 🔄 CDN加速

---

## 🐛 常见问题

### 1. 数据库连接失败
**解决方案**: 检查MySQL服务是否启动,配置文件中的数据库地址、用户名、密码是否正确

### 2. 前端跨域问题
**解决方案**: 确保后端CORS配置正确,或使用代理配置

### 3. 排课算法执行时间过长
**解决方案**: 调整遗传算法参数(种群大小、迭代次数),或优化数据量

### 4. 前端依赖安装失败
**解决方案**: 清除npm缓存 `npm cache clean --force`,使用淘宝镜像 `npm install --registry=https://registry.npm.taobao.org`

---

## 📝 更新日志

### v2.0.0 (2024-11-23)
- ✨ 全新UI设计,采用现代渐变色主题
- ✨ 优化用户体验,添加流畅动画效果
- ✨ 改进响应式设计,更好支持移动端
- ✨ 优化全局样式,统一组件风格
- 📝 完善项目文档

### v1.0.0 (2023-XX-XX)
- 🎉 项目初始版本
- ✅ 实现基本排课功能
- ✅ 完成用户管理模块
- ✅ 实现课表查询功能

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request!

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👥 开发团队

- **项目负责人**: CourseArrange Team
- **技术支持**: [GitHub Issues](https://github.com/your-repo/issues)

---

## 🙏 致谢

感谢所有为本项目做出贡献的开发者!

- Spring Boot
- Vue.js
- Element UI
- MyBatis-Plus

---

<div align="center">

**如果这个项目对你有帮助,请给一个 ⭐️ Star!**

Made with ❤️ by CourseArrange Team

</div>
