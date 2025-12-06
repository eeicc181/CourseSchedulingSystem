-- =============================================
-- 在线选课系统数据库表
-- 创建日期: 2024-12-06
-- 版本: 2.0.0
-- =============================================

-- 创建选课记录表
CREATE TABLE IF NOT EXISTS `tb_course_selection` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_no` VARCHAR(50) NOT NULL COMMENT '学生学号',
  `course_no` VARCHAR(50) NOT NULL COMMENT '课程编号',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期',
  `priority` INT(1) NOT NULL DEFAULT 1 COMMENT '志愿优先级：1-第一志愿, 2-第二志愿, 3-第三志愿',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '选课状态：PENDING-待处理, CONFIRMED-已确认, REJECTED-已拒绝, CANCELLED-已取消',
  `selection_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_no`),
  KEY `idx_course` (`course_no`),
  KEY `idx_semester` (`semester`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- 插入测试数据
INSERT INTO `tb_course_selection` (`student_no`, `course_no`, `semester`, `priority`, `status`, `selection_time`) VALUES
('2024010101', 'CS101', '2024-2025-1', 1, 'CONFIRMED', NOW()),
('2024010101', 'MATH201', '2024-2025-1', 2, 'CONFIRMED', NOW()),
('2024010102', 'CS101', '2024-2025-1', 1, 'PENDING', NOW());

-- 查询统计
SELECT 
    semester AS '学期',
    COUNT(*) AS '总选课数',
    SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) AS '已确认',
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS '待处理',
    SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS '已拒绝'
FROM tb_course_selection
WHERE deleted = 0
GROUP BY semester;
