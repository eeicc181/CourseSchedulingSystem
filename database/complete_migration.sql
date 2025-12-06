-- ========================================
-- 完整的密码迁移脚本
-- ========================================
-- 步骤 1：扩展密码字段长度
-- 步骤 2：备份原密码
-- 步骤 3：迁移密码为 BCrypt
-- 步骤 4：验证结果
-- ========================================

USE db_course_arrangement;

-- ========================================
-- 步骤 1：扩展密码字段长度（BCrypt 需要 60 字符）
-- ========================================

ALTER TABLE tb_admin MODIFY COLUMN password VARCHAR(100) NOT NULL COMMENT '密码';
ALTER TABLE tb_teacher MODIFY COLUMN password VARCHAR(100) NOT NULL COMMENT '密码';
ALTER TABLE tb_student MODIFY COLUMN password VARCHAR(100) NOT NULL COMMENT '密码';

SELECT '✓ 步骤 1：密码字段长度已扩展为 100 字符' AS status;

-- ========================================
-- 步骤 2：备份原密码
-- ========================================

DROP TABLE IF EXISTS tb_admin_password_backup;
CREATE TABLE tb_admin_password_backup AS 
SELECT id, admin_no, password, NOW() as backup_time FROM tb_admin;

DROP TABLE IF EXISTS tb_teacher_password_backup;
CREATE TABLE tb_teacher_password_backup AS 
SELECT id, teacher_no, password, NOW() as backup_time FROM tb_teacher;

DROP TABLE IF EXISTS tb_student_password_backup;
CREATE TABLE tb_student_password_backup AS 
SELECT id, student_no, password, NOW() as backup_time FROM tb_student;

SELECT '✓ 步骤 2：原密码已备份' AS status;

-- ========================================
-- 步骤 3：查看迁移前状态
-- ========================================

SELECT '【迁移前】管理员密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_admin;

SELECT '【迁移前】教师密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_teacher;

SELECT '【迁移前】学生密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_student;

-- ========================================
-- 步骤 4：迁移密码（BCrypt 加密的 "123456"）
-- ========================================

UPDATE tb_admin 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

UPDATE tb_teacher 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

UPDATE tb_student 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

SELECT '✓ 步骤 4：密码迁移完成' AS status;

-- ========================================
-- 步骤 5：验证迁移结果
-- ========================================

SELECT '【迁移后】管理员密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_admin;

SELECT '【迁移后】教师密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_teacher;

SELECT '【迁移后】学生密码状态：' AS info;
SELECT 
    COUNT(*) as 总数,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as BCrypt加密,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as 明文密码
FROM tb_student;

-- ========================================
-- 步骤 6：显示测试账号
-- ========================================

SELECT '【测试账号】' AS info;
SELECT 'admin' as 用户名, '123456' as 密码, '管理员' as 角色
UNION ALL
SELECT '10010', '123456', '教师'
UNION ALL
SELECT '2021040101', '123456', '学生';

-- ========================================
-- 完成提示
-- ========================================

SELECT 
    '✓✓✓ 密码迁移完成！✓✓✓' AS message;

SELECT 
    '所有用户密码已重置为：123456' AS 密码信息,
    '密码已使用 BCrypt 加密（60字符）' AS 安全信息,
    '原密码已备份到 *_password_backup 表' AS 备份信息;

-- 显示备份表信息
SELECT '【备份表信息】' AS info;
SELECT 'tb_admin_password_backup' as 备份表, COUNT(*) as 记录数 FROM tb_admin_password_backup
UNION ALL
SELECT 'tb_teacher_password_backup', COUNT(*) FROM tb_teacher_password_backup
UNION ALL
SELECT 'tb_student_password_backup', COUNT(*) FROM tb_student_password_backup;

-- 显示示例账号
SELECT '【示例账号 - 可直接登录】' AS info;
SELECT admin_no as 账号, '123456' as 密码, realname as 姓名, '管理员' as 类型 
FROM tb_admin LIMIT 3;

SELECT teacher_no as 账号, '123456' as 密码, realname as 姓名, '教师' as 类型 
FROM tb_teacher LIMIT 3;

SELECT student_no as 账号, '123456' as 密码, realname as 姓名, '学生' as 类型 
FROM tb_student LIMIT 3;
