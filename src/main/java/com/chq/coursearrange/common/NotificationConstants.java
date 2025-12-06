package com.chq.coursearrange.common;

/**
 * 消息通知常量
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class NotificationConstants {
    
    private NotificationConstants() {
        throw new IllegalStateException("Constant class");
    }
    
    /**
     * 消息类型
     */
    public static class Type {
        public static final String SYSTEM = "SYSTEM";      // 系统通知
        public static final String SCHEDULE = "SCHEDULE";  // 排课通知
        public static final String CHANGE = "CHANGE";      // 变更通知
        public static final String EXAM = "EXAM";          // 考试通知
        public static final String ANNOUNCEMENT = "ANNOUNCEMENT";  // 公告通知
    }
    
    /**
     * 接收者类型
     */
    public static class ReceiverType {
        public static final String ALL = "ALL";            // 全部用户
        public static final String TEACHER = "TEACHER";    // 教师
        public static final String STUDENT = "STUDENT";    // 学生
        public static final String USER = "USER";          // 指定用户
    }
    
    /**
     * 优先级
     */
    public static class Priority {
        public static final String LOW = "LOW";            // 低
        public static final String MEDIUM = "MEDIUM";      // 中
        public static final String HIGH = "HIGH";          // 高
        public static final String URGENT = "URGENT";      // 紧急
    }
    
    /**
     * 发送渠道
     */
    public static class Channel {
        public static final String SITE = "SITE";          // 站内消息
        public static final String EMAIL = "EMAIL";        // 邮件
        public static final String SMS = "SMS";            // 短信
        public static final String WECHAT = "WECHAT";      // 微信
    }
    
    /**
     * 发送状态
     */
    public static class Status {
        public static final String PENDING = "PENDING";    // 待发送
        public static final String SENT = "SENT";          // 已发送
        public static final String FAILED = "FAILED";      // 发送失败
    }
    
    /**
     * 阅读状态
     */
    public static class ReadStatus {
        public static final int UNREAD = 0;                // 未读
        public static final int READ = 1;                  // 已读
    }
}
