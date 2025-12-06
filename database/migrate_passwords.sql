-- ========================================
-- 密码迁移脚本（完整版）
-- ========================================
-- 功能：将所有明文密码迁移为 BCrypt 加密密码
-- BCrypt 加密后的 "123456": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- ========================================

USE db_course_arrangement;

-- 步骤 1：备份原密码（创建备份表）
DROP TABLE IF EXISTS tb_admin_password_backup;
CREATE TABLE tb_admin_password_backup AS 
SELECT id, admin_no, password, NOW() as backup_time FROM tb_admin;

DROP TABLE IF EXISTS tb_teacher_password_backup;
CREATE TABLE tb_teacher_password_backup AS 
SELECT id, teacher_no, password, NOW() as backup_time FROM tb_teacher;

DROP TABLE IF EXISTS tb_student_password_backup;
CREATE TABLE tb_student_password_backup AS 
SELECT id, student_no, password, NOW() as backup_time FROM tb_student;

SELECT '✓ 步骤 1：密码备份完成' AS status;

-- 步骤 2：查看当前密码状态
SELECT '当前管理员密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_admin;

SELECT '当前教师密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_teacher;

SELECT '当前学生密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_student;

-- 步骤 3：迁移密码（将明文密码改为 BCrypt 加密的 "123456"）
-- BCrypt 加密的 "123456"
UPDATE tb_admin 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

UPDATE tb_teacher 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

UPDATE tb_student 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKlRLIyS' 
WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%';

SELECT '✓ 步骤 3：密码迁移完成' AS status;

-- 步骤 4：验证迁移结果
SELECT '迁移后管理员密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_admin;

SELECT '迁移后教师密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_teacher;

SELECT '迁移后学生密码状态：' AS info;
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 1 ELSE 0 END) as bcrypt_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' THEN 1 ELSE 0 END) as plaintext_count
FROM tb_student;

-- 步骤 5：显示测试账号
SELECT '测试账号信息：' AS info;
SELECT 'admin' as username, '123456' as password, '管理员' as role
UNION ALL
SELECT '10010', '123456', '教师'
UNION ALL
SELECT '2021040101', '123456', '学生';

-- 完成提示
SELECT 
    '✓ 密码迁移完成！' AS message,
    '所有用户密码已重置为：123456' AS password_info,
    '密码已使用 BCrypt 加密' AS security_info;

-- 备份表信息
SELECT '备份表已创建：' AS info;
SELECT 'tb_admin_password_backup' as backup_table, COUNT(*) as record_count FROM tb_admin_password_backup
UNION ALL
SELECT 'tb_teacher_password_backup', COUNT(*) FROM tb_teacher_password_backup
UNION ALL
SELECT 'tb_student_password_backup', COUNT(*) FROM tb_student_password_backup;
