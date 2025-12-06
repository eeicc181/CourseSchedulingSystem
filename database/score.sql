-- =============================================
-- 成绩管理系统数据库表
-- 创建日期: 2024-12-06
-- 版本: 2.0.0
-- =============================================

-- 创建成绩表
CREATE TABLE IF NOT EXISTS `tb_score` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_no` VARCHAR(50) NOT NULL COMMENT '学生学号',
  `course_no` VARCHAR(50) NOT NULL COMMENT '课程编号',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期',
  `exam_id` INT(11) DEFAULT NULL COMMENT '考试ID',
  `usual_score` DECIMAL(5,2) DEFAULT NULL COMMENT '平时成绩',
  `midterm_score` DECIMAL(5,2) DEFAULT NULL COMMENT '期中成绩',
  `final_score` DECIMAL(5,2) DEFAULT NULL COMMENT '期末成绩',
  `total_score` DECIMAL(5,2) DEFAULT NULL COMMENT '总评成绩',
  `grade_point` DECIMAL(3,2) DEFAULT NULL COMMENT '绩点',
  `grade` VARCHAR(2) DEFAULT NULL COMMENT '等级：A/B/C/D/F',
  `passed` TINYINT(1) DEFAULT NULL COMMENT '是否通过：0-未通过, 1-通过',
  `teacher_no` VARCHAR(50) DEFAULT NULL COMMENT '录入教师',
  `input_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_no`),
  KEY `idx_course` (`course_no`),
  KEY `idx_semester` (`semester`),
  KEY `idx_exam` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 插入测试数据
INSERT INTO `tb_score` (`student_no`, `course_no`, `semester`, `usual_score`, `midterm_score`, `final_score`, `total_score`, `grade_point`, `grade`, `passed`, `teacher_no`) VALUES
('2024010101', 'MATH201', '2024-2025-1', 85.0, 88.0, 90.0, 88.0, 3.8, 'A', 1, 'T001'),
('2024010101', 'CS102', '2024-2025-1', 90.0, 92.0, 95.0, 92.5, 4.0, 'A', 1, 'T002'),
('2024010102', 'MATH201', '2024-2025-1', 75.0, 78.0, 80.0, 78.0, 3.0, 'B', 1, 'T001'),
('2024010102', 'CS102', '2024-2025-1', 82.0, 85.0, 88.0, 85.5, 3.5, 'B', 1, 'T002'),
('2024010103', 'MATH201', '2024-2025-1', 55.0, 58.0, 60.0, 58.0, 1.5, 'D', 0, 'T001');

-- 查询统计
SELECT 
    semester AS '学期',
    course_no AS '课程',
    COUNT(*) AS '总人数',
    AVG(total_score) AS '平均分',
    MAX(total_score) AS '最高分',
    MIN(total_score) AS '最低分',
    SUM(CASE WHEN passed = 1 THEN 1 ELSE 0 END) AS '通过人数',
    CONCAT(ROUND(SUM(CASE WHEN passed = 1 THEN 1 ELSE 0 END) / COUNT(*) * 100, 2), '%') AS '通过率'
FROM tb_score
WHERE deleted = 0
GROUP BY semester, course_no;
