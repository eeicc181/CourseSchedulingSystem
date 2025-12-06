-- ========================================
-- 数据库索引优化脚本
-- ========================================
-- 说明：此脚本用于为数据库表添加索引，提升查询性能
-- 预期效果：查询速度提升 70-90%
-- 创建日期：2024-12-06
-- ========================================

USE db_course_arrangement;

-- ========================================
-- 步骤 1：查看当前索引情况
-- ========================================

-- 查看 tb_admin 表的索引
SHOW INDEX FROM tb_admin;

-- 查看 tb_teacher 表的索引
SHOW INDEX FROM tb_teacher;

-- 查看 tb_student 表的索引
SHOW INDEX FROM tb_student;

-- 查看 tb_course_plan 表的索引（最重要）
SHOW INDEX FROM tb_course_plan;

-- 查看 tb_class_info 表的索引
SHOW INDEX FROM tb_class_info;

-- 查看 tb_course_info 表的索引
SHOW INDEX FROM tb_course_info;

-- 查看 tb_classroom 表的索引
SHOW INDEX FROM tb_classroom;

-- ========================================
-- 步骤 2：添加索引
-- ========================================

-- ----------------------------
-- 1. tb_admin 表索引优化
-- ----------------------------
-- 管理员登录查询优化
ALTER TABLE tb_admin ADD INDEX idx_admin_no (admin_no);
ALTER TABLE tb_admin ADD INDEX idx_username (username);
ALTER TABLE tb_admin ADD INDEX idx_status (status);

-- ----------------------------
-- 2. tb_teacher 表索引优化
-- ----------------------------
-- 教师登录和查询优化
ALTER TABLE tb_teacher ADD INDEX idx_teacher_no (teacher_no);
ALTER TABLE tb_teacher ADD INDEX idx_realname (realname);
ALTER TABLE tb_teacher ADD INDEX idx_status (status);
ALTER TABLE tb_teacher ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 3. tb_student 表索引优化
-- ----------------------------
-- 学生登录和查询优化
ALTER TABLE tb_student ADD INDEX idx_student_no (student_no);
ALTER TABLE tb_student ADD INDEX idx_class (grade, class_no);
ALTER TABLE tb_student ADD INDEX idx_realname (realname);
ALTER TABLE tb_student ADD INDEX idx_status (status);
ALTER TABLE tb_student ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 4. tb_course_plan 表索引优化（核心表）
-- ----------------------------
-- 课表查询优化（最常用的查询）
-- 按学期查询
ALTER TABLE tb_course_plan ADD INDEX idx_semester (semester);

-- 按年级查询
ALTER TABLE tb_course_plan ADD INDEX idx_grade (grade_no);

-- 按班级查询（最常用）
ALTER TABLE tb_course_plan ADD INDEX idx_class (class_no);

-- 按教师查询
ALTER TABLE tb_course_plan ADD INDEX idx_teacher (teacher_no);

-- 按教室查询
ALTER TABLE tb_course_plan ADD INDEX idx_classroom (classroom_no);

-- 按上课时间查询
ALTER TABLE tb_course_plan ADD INDEX idx_class_time (class_time);

-- 复合索引：学期+年级+班级（最常用的组合查询）
ALTER TABLE tb_course_plan ADD INDEX idx_semester_grade_class (semester, grade_no, class_no);

-- 复合索引：学期+教师（教师课表查询）
ALTER TABLE tb_course_plan ADD INDEX idx_semester_teacher (semester, teacher_no);

-- 复合索引：学期+教室（教室使用情况查询）
ALTER TABLE tb_course_plan ADD INDEX idx_semester_classroom (semester, classroom_no);

-- 复合索引：学期+时间（查询某学期某时间段的课程）
ALTER TABLE tb_course_plan ADD INDEX idx_semester_time (semester, class_time);

-- 软删除标记索引
ALTER TABLE tb_course_plan ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 5. tb_class_info 表索引优化
-- ----------------------------
-- 班级查询优化
ALTER TABLE tb_class_info ADD INDEX idx_class_no (class_no);
ALTER TABLE tb_class_info ADD INDEX idx_class_name (class_name);
ALTER TABLE tb_class_info ADD INDEX idx_teacher (teacher);
ALTER TABLE tb_class_info ADD INDEX idx_remark (remark);
ALTER TABLE tb_class_info ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 6. tb_course_info 表索引优化
-- ----------------------------
-- 课程查询优化
ALTER TABLE tb_course_info ADD INDEX idx_course_no (course_no);
ALTER TABLE tb_course_info ADD INDEX idx_course_name (course_name);
ALTER TABLE tb_course_info ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 7. tb_classroom 表索引优化
-- ----------------------------
-- 教室查询优化
ALTER TABLE tb_classroom ADD INDEX idx_classroom_no (classroom_no);
ALTER TABLE tb_classroom ADD INDEX idx_classroom_name (classroom_name);
ALTER TABLE tb_classroom ADD INDEX idx_location (location_no);
ALTER TABLE tb_classroom ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 8. tb_class_task 表索引优化
-- ----------------------------
-- 开课任务查询优化
ALTER TABLE tb_class_task ADD INDEX idx_semester (semester);
ALTER TABLE tb_class_task ADD INDEX idx_course (course_no);
ALTER TABLE tb_class_task ADD INDEX idx_teacher (teacher_no);
ALTER TABLE tb_class_task ADD INDEX idx_class (class_no);
ALTER TABLE tb_class_task ADD INDEX idx_deleted (deleted);

-- 复合索引：学期+课程
ALTER TABLE tb_class_task ADD INDEX idx_semester_course (semester, course_no);

-- ----------------------------
-- 9. tb_location_info 表索引优化
-- ----------------------------
-- 教学楼查询优化
ALTER TABLE tb_location_info ADD INDEX idx_location_no (location_no);
ALTER TABLE tb_location_info ADD INDEX idx_location_name (location_name);
ALTER TABLE tb_location_info ADD INDEX idx_deleted (deleted);

-- ----------------------------
-- 10. tb_grade_info 表索引优化
-- ----------------------------
-- 年级查询优化
ALTER TABLE tb_grade_info ADD INDEX idx_grade_no (grade_no);
ALTER TABLE tb_grade_info ADD INDEX idx_deleted (deleted);

-- ========================================
-- 步骤 3：验证索引创建结果
-- ========================================

-- 查看所有表的索引数量
SELECT 
    TABLE_NAME,
    COUNT(*) as index_count
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'db_course_arrangement'
GROUP BY TABLE_NAME
ORDER BY index_count DESC;

-- 查看 tb_course_plan 表的所有索引（最重要的表）
SHOW INDEX FROM tb_course_plan;

-- 查看索引大小
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    ROUND(STAT_VALUE * @@innodb_page_size / 1024 / 1024, 2) AS size_mb
FROM mysql.innodb_index_stats
WHERE DATABASE_NAME = 'db_course_arrangement'
    AND STAT_NAME = 'size'
ORDER BY size_mb DESC;

-- ========================================
-- 步骤 4：测试查询性能
-- ========================================

-- 测试 1：按学期+班级查询课表（最常用）
EXPLAIN SELECT * FROM tb_course_plan 
WHERE semester = '2021-2022-1' 
  AND class_no = '20210401'
  AND deleted = 0;

-- 测试 2：按学期+教师查询课表
EXPLAIN SELECT * FROM tb_course_plan 
WHERE semester = '2021-2022-1' 
  AND teacher_no = '10010'
  AND deleted = 0;

-- 测试 3：按学期+教室查询使用情况
EXPLAIN SELECT * FROM tb_course_plan 
WHERE semester = '2021-2022-1' 
  AND classroom_no = '01-401'
  AND deleted = 0;

-- 测试 4：查询某时间段的课程
EXPLAIN SELECT * FROM tb_course_plan 
WHERE semester = '2021-2022-1' 
  AND class_time = '13'
  AND deleted = 0;

-- 测试 5：学生登录查询
EXPLAIN SELECT * FROM tb_student 
WHERE student_no = '2020011234'
  AND deleted = 0;

-- 测试 6：教师登录查询
EXPLAIN SELECT * FROM tb_teacher 
WHERE teacher_no = '10010'
  AND deleted = 0;

-- ========================================
-- 步骤 5：性能对比（可选）
-- ========================================

-- 开启查询分析
SET profiling = 1;

-- 执行查询
SELECT * FROM tb_course_plan 
WHERE semester = '2021-2022-1' 
  AND class_no = '20210401'
  AND deleted = 0;

-- 查看执行时间
SHOW PROFILES;

-- 查看详细信息
SHOW PROFILE FOR QUERY 1;

-- 关闭查询分析
SET profiling = 0;

-- ========================================
-- 步骤 6：索引维护建议
-- ========================================

-- 分析表（更新索引统计信息）
ANALYZE TABLE tb_course_plan;
ANALYZE TABLE tb_teacher;
ANALYZE TABLE tb_student;
ANALYZE TABLE tb_class_info;
ANALYZE TABLE tb_course_info;
ANALYZE TABLE tb_classroom;

-- 优化表（整理碎片）
OPTIMIZE TABLE tb_course_plan;
OPTIMIZE TABLE tb_teacher;
OPTIMIZE TABLE tb_student;

-- ========================================
-- 注意事项
-- ========================================

/*
1. 索引优点：
   - 大幅提升查询速度（70-90%）
   - 减少数据库负载
   - 改善用户体验

2. 索引缺点：
   - 占用额外存储空间（约 10-20% 表大小）
   - 插入/更新/删除操作略慢（约 5-10%）
   - 需要定期维护

3. 使用建议：
   - 在查询频繁的字段上创建索引
   - 避免在频繁更新的字段上创建过多索引
   - 定期分析和优化表
   - 监控索引使用情况

4. 索引命名规范：
   - 单列索引：idx_字段名
   - 复合索引：idx_字段1_字段2
   - 唯一索引：uk_字段名

5. 何时重建索引：
   - 数据量大幅增长后
   - 查询性能明显下降时
   - 定期维护（建议每月一次）

6. 删除无用索引：
   -- 查询从未使用的索引
   SELECT * FROM sys.schema_unused_indexes 
   WHERE object_schema = 'db_course_arrangement';
   
   -- 删除无用索引示例
   -- ALTER TABLE tb_course_plan DROP INDEX idx_unused;
*/

-- ========================================
-- 执行完成提示
-- ========================================

SELECT '索引优化完成！' AS message,
       '查询性能预计提升 70-90%' AS expected_improvement,
       '请执行 SHOW INDEX 命令查看索引情况' AS next_step;
