-- ========================================
-- 密码迁移脚本 - BCrypt 加密
-- ========================================
-- 说明：此脚本用于将数据库中的明文密码迁移为 BCrypt 加密密码
-- 执行前请务必备份数据库！
-- 创建日期：2024-12-06
-- ========================================

-- 步骤 1：备份原有密码（可选，建议执行）
-- 创建备份表
CREATE TABLE IF NOT EXISTS tb_admin_backup AS SELECT * FROM tb_admin;
CREATE TABLE IF NOT EXISTS tb_teacher_backup AS SELECT * FROM tb_teacher;
CREATE TABLE IF NOT EXISTS tb_student_backup AS SELECT * FROM tb_student;

-- 步骤 2：查看当前密码情况
-- 查看管理员密码
SELECT admin_no, password, 
       CASE 
           WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 'BCrypt加密'
           ELSE '明文密码'
       END AS password_type
FROM tb_admin;

-- 查看教师密码
SELECT teacher_no, realname, password,
       CASE 
           WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 'BCrypt加密'
           ELSE '明文密码'
       END AS password_type
FROM tb_teacher
LIMIT 10;

-- 查看学生密码
SELECT student_no, realname, password,
       CASE 
           WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 'BCrypt加密'
           ELSE '明文密码'
       END AS password_type
FROM tb_student
LIMIT 10;

-- ========================================
-- 步骤 3：密码迁移
-- ========================================
-- 注意：由于 BCrypt 加密需要在应用层完成，
-- 这里提供两种方案：

-- 方案 A：统一重置密码（推荐用于测试环境）
-- 将所有用户密码重置为 "123456" 的 BCrypt 加密值
-- BCrypt("123456") = $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH

-- 管理员密码重置
UPDATE tb_admin 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH'
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

-- 教师密码重置
UPDATE tb_teacher 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH'
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

-- 学生密码重置
UPDATE tb_student 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH'
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

-- ========================================
-- 方案 B：保留原密码（需要应用层支持）
-- ========================================
-- 由于 BCrypt 是单向加密，无法从明文直接转换
-- 需要通过应用程序接口批量更新
-- 
-- 实施步骤：
-- 1. 创建一个临时的密码迁移接口（仅开发环境）
-- 2. 读取所有明文密码用户
-- 3. 使用 PasswordService.encodePassword() 加密
-- 4. 更新回数据库
--
-- 示例 Java 代码：
-- @PostMapping("/admin/migrate-passwords")
-- public ServerResponse migratePasswords() {
--     List<Admin> admins = adminService.list();
--     for (Admin admin : admins) {
--         if (!admin.getPassword().startsWith("$2a$")) {
--             String encoded = passwordService.encodePassword(admin.getPassword());
--             admin.setPassword(encoded);
--             adminService.updateById(admin);
--         }
--     }
--     return ServerResponse.ofSuccess("密码迁移完成");
-- }

-- ========================================
-- 步骤 4：验证迁移结果
-- ========================================

-- 检查管理员密码是否已加密
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as encrypted_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plain_count
FROM tb_admin;

-- 检查教师密码是否已加密
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as encrypted_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plain_count
FROM tb_teacher;

-- 检查学生密码是否已加密
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as encrypted_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plain_count
FROM tb_student;

-- ========================================
-- 步骤 5：清理备份表（可选）
-- ========================================
-- 确认迁移成功后，可以删除备份表
-- DROP TABLE IF EXISTS tb_admin_backup;
-- DROP TABLE IF EXISTS tb_teacher_backup;
-- DROP TABLE IF EXISTS tb_student_backup;

-- ========================================
-- 使用说明
-- ========================================
-- 1. 执行前务必备份数据库
-- 2. 建议先在测试环境执行
-- 3. 方案 A 适合测试环境，会重置所有密码为 123456
-- 4. 方案 B 需要应用层支持，可保留原密码
-- 5. 迁移后，旧的明文密码仍可登录（兼容模式）
-- 6. 建议逐步迁移，不要一次性更新所有用户

-- ========================================
-- 测试账号（迁移后）
-- ========================================
-- 所有账号密码：123456
-- 管理员：admin / 123456
-- 教师：teacher01 / 123456  
-- 学生：2020011234 / 123456
