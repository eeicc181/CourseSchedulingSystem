-- =============================================
-- 消息通知表
-- 创建日期: 2024-12-06
-- 版本: 2.0.0
-- =============================================

-- 创建消息通知表
CREATE TABLE IF NOT EXISTS `tb_notification` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `type` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '消息类型：SYSTEM-系统通知, SCHEDULE-排课通知, CHANGE-变更通知, EXAM-考试通知, ANNOUNCEMENT-公告通知',
  `receiver_type` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '接收者类型：ALL-全部, TEACHER-教师, STUDENT-学生, USER-指定用户',
  `receiver_id` VARCHAR(50) DEFAULT NULL COMMENT '接收者ID（当receiver_type为USER时使用）',
  `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW-低, MEDIUM-中, HIGH-高, URGENT-紧急',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读, 1-已读',
  `channel` VARCHAR(20) NOT NULL DEFAULT 'SITE' COMMENT '发送渠道：SITE-站内, EMAIL-邮件, SMS-短信, WECHAT-微信',
  `status` VARCHAR(20) NOT NULL DEFAULT 'SENT' COMMENT '发送状态：PENDING-待发送, SENT-已发送, FAILED-发送失败',
  `related_id` VARCHAR(50) DEFAULT NULL COMMENT '关联数据ID（如课程ID、考试ID等）',
  `related_type` VARCHAR(50) DEFAULT NULL COMMENT '关联数据类型（如COURSE、EXAM等）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_receiver` (`receiver_type`, `receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 插入测试数据
INSERT INTO `tb_notification` (`title`, `content`, `type`, `receiver_type`, `priority`, `is_read`, `channel`, `status`, `create_time`, `send_time`) VALUES
('系统维护通知', '系统将于今晚22:00-24:00进行维护升级，期间可能无法访问，请提前做好准备。', 'SYSTEM', 'ALL', 'HIGH', 0, 'SITE', 'SENT', NOW(), NOW()),
('排课完成通知', '2024-2025学年第一学期的课程安排已完成，请查看您的课表。', 'SCHEDULE', 'ALL', 'HIGH', 0, 'SITE', 'SENT', NOW(), NOW()),
('欢迎使用', '欢迎使用课程排课系统！如有问题请联系管理员。', 'SYSTEM', 'ALL', 'MEDIUM', 0, 'SITE', 'SENT', NOW(), NOW());

-- 查询统计
SELECT 
    type AS '消息类型',
    COUNT(*) AS '数量',
    SUM(CASE WHEN is_read = 0 THEN 1 ELSE 0 END) AS '未读数量',
    SUM(CASE WHEN is_read = 1 THEN 1 ELSE 0 END) AS '已读数量'
FROM tb_notification
WHERE deleted = 0
GROUP BY type;
