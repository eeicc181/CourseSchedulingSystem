-- ========================================
-- 数据库索引优化脚本（安全版本）
-- ========================================
-- 说明：此脚本用于为数据库表添加索引，提升查询性能
-- 预期效果：查询速度提升 70-90%
-- 创建日期：2024-12-06
-- ========================================

USE db_course_arrangement;

-- ========================================
-- 步骤 1：添加索引（忽略已存在的索引）
-- ========================================

-- ----------------------------
-- 1. tb_admin 表索引优化
-- ----------------------------
ALTER TABLE tb_admin ADD INDEX IF NOT EXISTS idx_admin_no (admin_no);
ALTER TABLE tb_admin ADD INDEX IF NOT EXISTS idx_username (username);
ALTER TABLE tb_admin ADD INDEX IF NOT EXISTS idx_status (status);

-- ----------------------------
-- 2. tb_teacher 表索引优化
-- ----------------------------
ALTER TABLE tb_teacher ADD INDEX IF NOT EXISTS idx_teacher_no (teacher_no);
ALTER TABLE tb_teacher ADD INDEX IF NOT EXISTS idx_realname (realname);
ALTER TABLE tb_teacher ADD INDEX IF NOT EXISTS idx_status (status);
ALTER TABLE tb_teacher ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 3. tb_student 表索引优化
-- ----------------------------
ALTER TABLE tb_student ADD INDEX IF NOT EXISTS idx_student_no (student_no);
ALTER TABLE tb_student ADD INDEX IF NOT EXISTS idx_class (class_no);
ALTER TABLE tb_student ADD INDEX IF NOT EXISTS idx_realname (realname);
ALTER TABLE tb_student ADD INDEX IF NOT EXISTS idx_status (status);
ALTER TABLE tb_student ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 4. tb_course_plan 表索引优化（核心表）
-- ----------------------------
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_semester (semester);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_grade (grade_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_class (class_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_teacher (teacher_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_classroom (classroom_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_class_time (class_time);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_semester_grade_class (semester, grade_no, class_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_semester_teacher (semester, teacher_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_semester_classroom (semester, classroom_no);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_semester_time (semester, class_time);
ALTER TABLE tb_course_plan ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 5. tb_class_info 表索引优化
-- ----------------------------
ALTER TABLE tb_class_info ADD INDEX IF NOT EXISTS idx_class_no (class_no);
ALTER TABLE tb_class_info ADD INDEX IF NOT EXISTS idx_class_name (class_name);
ALTER TABLE tb_class_info ADD INDEX IF NOT EXISTS idx_teacher (teacher);
ALTER TABLE tb_class_info ADD INDEX IF NOT EXISTS idx_remark (remark);
ALTER TABLE tb_class_info ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 6. tb_course_info 表索引优化
-- ----------------------------
ALTER TABLE tb_course_info ADD INDEX IF NOT EXISTS idx_course_no (course_no);
ALTER TABLE tb_course_info ADD INDEX IF NOT EXISTS idx_course_name (course_name);
ALTER TABLE tb_course_info ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 7. tb_classroom 表索引优化
-- ----------------------------
ALTER TABLE tb_classroom ADD INDEX IF NOT EXISTS idx_classroom_no (classroom_no);
ALTER TABLE tb_classroom ADD INDEX IF NOT EXISTS idx_classroom_name (classroom_name);
ALTER TABLE tb_classroom ADD INDEX IF NOT EXISTS idx_deleted (deleted);

-- ----------------------------
-- 8. tb_class_task 表索引优化
-- ----------------------------
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_semester (semester);
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_course (course_no);
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_teacher (teacher_no);
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_class (class_no);
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_deleted (deleted);
ALTER TABLE tb_class_task ADD INDEX IF NOT EXISTS idx_semester_course (semester, course_no);

-- ========================================
-- 步骤 2：验证索引创建结果
-- ========================================

SELECT '索引优化完成！' AS message,
       '查询性能预计提升 70-90%' AS expected_improvement;

-- 查看所有表的索引数量
SELECT 
    TABLE_NAME,
    COUNT(*) as index_count
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'db_course_arrangement'
GROUP BY TABLE_NAME
ORDER BY index_count DESC;
