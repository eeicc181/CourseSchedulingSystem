-- ========================================
-- 数据库索引优化脚本（兼容版本）
-- ========================================

USE db_course_arrangement;

-- tb_admin 表索引
ALTER TABLE tb_admin ADD INDEX idx_admin_no (admin_no);
ALTER TABLE tb_admin ADD INDEX idx_username (username);
ALTER TABLE tb_admin ADD INDEX idx_status (status);

-- tb_teacher 表索引
ALTER TABLE tb_teacher ADD INDEX idx_teacher_no (teacher_no);
ALTER TABLE tb_teacher ADD INDEX idx_realname (realname);
ALTER TABLE tb_teacher ADD INDEX idx_status_teacher (status);
ALTER TABLE tb_teacher ADD INDEX idx_deleted_teacher (deleted);

-- tb_student 表索引
ALTER TABLE tb_student ADD INDEX idx_student_no (student_no);
ALTER TABLE tb_student ADD INDEX idx_class_student (class_no);
ALTER TABLE tb_student ADD INDEX idx_realname_student (realname);
ALTER TABLE tb_student ADD INDEX idx_status_student (status);
ALTER TABLE tb_student ADD INDEX idx_deleted_student (deleted);

-- tb_course_plan 表索引（核心表）
ALTER TABLE tb_course_plan ADD INDEX idx_semester (semester);
ALTER TABLE tb_course_plan ADD INDEX idx_grade (grade_no);
ALTER TABLE tb_course_plan ADD INDEX idx_class_plan (class_no);
ALTER TABLE tb_course_plan ADD INDEX idx_teacher_plan (teacher_no);
ALTER TABLE tb_course_plan ADD INDEX idx_classroom (classroom_no);
ALTER TABLE tb_course_plan ADD INDEX idx_class_time (class_time);
ALTER TABLE tb_course_plan ADD INDEX idx_semester_grade_class (semester, grade_no, class_no);
ALTER TABLE tb_course_plan ADD INDEX idx_semester_teacher (semester, teacher_no);
ALTER TABLE tb_course_plan ADD INDEX idx_semester_classroom (semester, classroom_no);
ALTER TABLE tb_course_plan ADD INDEX idx_semester_time (semester, class_time);
ALTER TABLE tb_course_plan ADD INDEX idx_deleted_plan (deleted);

-- tb_class_info 表索引
ALTER TABLE tb_class_info ADD INDEX idx_class_no (class_no);
ALTER TABLE tb_class_info ADD INDEX idx_class_name (class_name);
ALTER TABLE tb_class_info ADD INDEX idx_teacher_class (teacher);
ALTER TABLE tb_class_info ADD INDEX idx_remark (remark);
ALTER TABLE tb_class_info ADD INDEX idx_deleted_class (deleted);

-- tb_course_info 表索引
ALTER TABLE tb_course_info ADD INDEX idx_course_no (course_no);
ALTER TABLE tb_course_info ADD INDEX idx_course_name (course_name);
ALTER TABLE tb_course_info ADD INDEX idx_deleted_course (deleted);

-- tb_classroom 表索引
ALTER TABLE tb_classroom ADD INDEX idx_classroom_no (classroom_no);
ALTER TABLE tb_classroom ADD INDEX idx_classroom_name (classroom_name);
ALTER TABLE tb_classroom ADD INDEX idx_deleted_classroom (deleted);

-- tb_class_task 表索引
ALTER TABLE tb_class_task ADD INDEX idx_semester_task (semester);
ALTER TABLE tb_class_task ADD INDEX idx_course_task (course_no);
ALTER TABLE tb_class_task ADD INDEX idx_teacher_task (teacher_no);
ALTER TABLE tb_class_task ADD INDEX idx_class_task (class_no);
ALTER TABLE tb_class_task ADD INDEX idx_deleted_task (deleted);
ALTER TABLE tb_class_task ADD INDEX idx_semester_course (semester, course_no);

SELECT '索引优化完成！' AS message;
