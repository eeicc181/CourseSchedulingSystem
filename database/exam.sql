-- =============================================
-- 考试安排管理系统数据库表
-- 创建日期: 2024-12-06
-- 版本: 2.0.0
-- =============================================

-- 创建考试表
CREATE TABLE IF NOT EXISTS `tb_exam` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_name` VARCHAR(200) NOT NULL COMMENT '考试名称',
  `course_no` VARCHAR(50) NOT NULL COMMENT '课程编号',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期',
  `exam_type` VARCHAR(20) NOT NULL DEFAULT 'FINAL' COMMENT '考试类型：MIDTERM-期中, FINAL-期末, MAKEUP-补考, RETAKE-重修',
  `exam_time` DATETIME NOT NULL COMMENT '考试时间',
  `duration` INT(11) NOT NULL DEFAULT 120 COMMENT '考试时长（分钟）',
  `classroom_no` VARCHAR(50) DEFAULT NULL COMMENT '考场编号',
  `invigilators` VARCHAR(500) DEFAULT NULL COMMENT '监考教师编号（多个用逗号分隔）',
  `class_nos` VARCHAR(500) DEFAULT NULL COMMENT '参考班级（多个用逗号分隔）',
  `status` VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT '考试状态：SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_course` (`course_no`),
  KEY `idx_semester` (`semester`),
  KEY `idx_exam_time` (`exam_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 创建考试学生关联表
CREATE TABLE IF NOT EXISTS `tb_exam_student` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` INT(11) NOT NULL COMMENT '考试ID',
  `student_no` VARCHAR(50) NOT NULL COMMENT '学生学号',
  `seat_no` VARCHAR(20) DEFAULT NULL COMMENT '座位号',
  `admission_no` VARCHAR(50) DEFAULT NULL COMMENT '准考证号',
  `absent` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否缺考：0-未缺考, 1-缺考',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '成绩',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_exam` (`exam_id`),
  KEY `idx_student` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试学生关联表';

-- 插入测试数据
INSERT INTO `tb_exam` (`exam_name`, `course_no`, `semester`, `exam_type`, `exam_time`, `duration`, `classroom_no`, `invigilators`, `class_nos`, `status`) VALUES
('高等数学期末考试', 'MATH201', '2024-2025-1', 'FINAL', '2025-01-10 09:00:00', 120, 'A101,A102', 'T001,T002', '20240101,20240102', 'SCHEDULED'),
('数据结构期中考试', 'CS102', '2024-2025-1', 'MIDTERM', '2024-11-15 14:00:00', 90, 'B201', 'T003', '20240101', 'COMPLETED'),
('大学英语期末考试', 'ENG101', '2024-2025-1', 'FINAL', '2025-01-12 09:00:00', 120, 'A201,A202,A203', 'T004,T005,T006', '20240101,20240102,20240103', 'SCHEDULED');

INSERT INTO `tb_exam_student` (`exam_id`, `student_no`, `seat_no`, `admission_no`, `absent`, `score`) VALUES
(2, '2024010101', 'A01', '202401010101', 0, 85.5),
(2, '2024010102', 'A02', '202401010102', 0, 92.0),
(2, '2024010103', 'A03', '202401010103', 1, NULL);

-- 查询统计
SELECT 
    semester AS '学期',
    exam_type AS '考试类型',
    COUNT(*) AS '考试场次',
    SUM(CASE WHEN status = 'SCHEDULED' THEN 1 ELSE 0 END) AS '已安排',
    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS '已完成'
FROM tb_exam
WHERE deleted = 0
GROUP BY semester, exam_type;
