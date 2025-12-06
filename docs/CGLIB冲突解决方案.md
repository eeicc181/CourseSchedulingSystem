# CGLIB 类加载冲突解决方案

## 🔍 问题描述

**错误信息**：
```
java.lang.LinkageError: loader 'app' attempted duplicate class definition 
for com.baomidou.mybatisplus.extension.service.IService$$FastClassBySpringCGLIB$$f8525d18
```

**原因**：Spring DevTools 热部署导致 CGLIB 动态代理类被重复加载

---

## ✅ 推荐解决方案（保留热部署）

### 方案1：重启服务清除缓存 ⭐ 推荐

每次遇到此错误时：

```bash
# 停止服务（Ctrl+C）
# 清理缓存
mvn clean

# 重新启动
mvn spring-boot:run
```

**优点**：
- ✅ 保留热部署功能
- ✅ 彻底清除类加载缓存
- ✅ 不影响开发效率

**使用场景**：偶尔出现此错误时

---

### 方案2：使用 IDEA 重新构建

如果使用 IntelliJ IDEA：

1. 点击菜单：`Build` → `Rebuild Project`
2. 重启应用

**优点**：
- ✅ IDE 集成，操作简单
- ✅ 自动清理编译缓存

---

### 方案3：添加 JVM 参数

在启动时添加参数：

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xverify:none"
```

或在 `pom.xml` 中配置：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>-Xverify:none</jvmArguments>
    </configuration>
</plugin>
```

**优点**：
- ✅ 减少类验证，降低冲突概率
- ✅ 保留热部署

**缺点**：
- ⚠️ 可能隐藏其他类加载问题

---

### 方案4：优化热部署配置 ⭐ 已应用

已在 `application-dev.yml` 中优化配置：

```yaml
spring:
  devtools:
    restart:
      enabled: true
      additional-paths: src/main/java
      exclude: WEB-INF/**,static/**,public/**
      trigger-file: .reloadtrigger
    livereload:
      enabled: true
```

**优点**：
- ✅ 减少不必要的重启
- ✅ 提高热部署稳定性
- ✅ 保留完整功能

---

## 🚫 不推荐的方案

### ❌ 完全禁用热部署

```yaml
spring:
  devtools:
    restart:
      enabled: false
```

**缺点**：
- ❌ 失去热部署便利性
- ❌ 每次修改都需手动重启
- ❌ 降低开发效率

---

## 🎯 最佳实践

### 日常开发流程

1. **正常开发**：修改代码，热部署自动生效
2. **遇到 CGLIB 错误**：
   - 停止服务（Ctrl+C）
   - 运行 `mvn clean`
   - 重新启动
3. **继续开发**：热部署恢复正常

### 预防措施

1. **定期清理**：每天开始工作前运行 `mvn clean`
2. **避免频繁重启**：减少不必要的代码修改
3. **使用触发文件**：通过 `.reloadtrigger` 控制重启时机

---

## 📊 方案对比

| 方案 | 保留热部署 | 解决彻底性 | 操作复杂度 | 推荐度 |
|------|-----------|-----------|-----------|--------|
| 重启+清理 | ✅ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| IDEA重构建 | ✅ | ⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐⭐ |
| JVM参数 | ✅ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| 优化配置 | ✅ | ⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐⭐ |
| 禁用热部署 | ❌ | ⭐⭐⭐⭐⭐ | ⭐ | ⭐ |

---

## 🔧 快速修复脚本

已创建 `restart-backend.bat`：

```batch
@echo off
echo 清理并重启后端服务...
call mvn clean
call mvn spring-boot:run
```

**使用方法**：双击运行即可

---

## 💡 深入理解

### 为什么会出现这个问题？

1. **热部署机制**：
   - Spring DevTools 使用两个类加载器
   - Base ClassLoader：加载不变的类（如第三方库）
   - Restart ClassLoader：加载应用代码

2. **CGLIB 动态代理**：
   - MyBatis Plus 使用 CGLIB 为 Service 生成代理类
   - 代理类名包含随机哈希值

3. **冲突原因**：
   - 热部署重启时，Restart ClassLoader 被替换
   - 但 CGLIB 缓存中仍保留旧的代理类
   - 新旧代理类名称相同，导致重复定义

### 为什么 `mvn clean` 有效？

- 清除所有编译缓存
- 删除 target 目录
- 强制重新编译所有类
- 清空 CGLIB 代理缓存

---

## 📚 相关资源

- [Spring DevTools 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [MyBatis Plus 文档](https://baomidou.com/)
- [CGLIB 原理](https://github.com/cglib/cglib)

---

## ✅ 总结

**推荐做法**：
1. ✅ 保留热部署功能
2. ✅ 遇到错误时运行 `mvn clean` 重启
3. ✅ 使用优化后的配置
4. ✅ 定期清理缓存

**不推荐**：
- ❌ 禁用热部署
- ❌ 忽略错误继续开发

---

**最后更新**: 2024-11-23  
**适用版本**: Spring Boot 2.x + MyBatis Plus 3.x
