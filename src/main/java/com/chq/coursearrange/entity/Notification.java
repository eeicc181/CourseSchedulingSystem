package com.chq.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息通知实体
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
@TableName("tb_notification")
public class Notification implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：SYSTEM-系统通知, SCHEDULE-排课通知, CHANGE-变更通知, EXAM-考试通知
     */
    private String type;
    
    /**
     * 接收者类型：ALL-全部, TEACHER-教师, STUDENT-学生, USER-指定用户
     */
    private String receiverType;
    
    /**
     * 接收者ID（当receiverType为USER时使用）
     */
    private String receiverId;
    
    /**
     * 优先级：LOW-低, MEDIUM-中, HIGH-高, URGENT-紧急
     */
    private String priority;
    
    /**
     * 是否已读：0-未读, 1-已读
     */
    private Integer isRead;
    
    /**
     * 发送渠道：SITE-站内, EMAIL-邮件, SMS-短信, WECHAT-微信
     */
    private String channel;
    
    /**
     * 发送状态：PENDING-待发送, SENT-已发送, FAILED-发送失败
     */
    private String status;
    
    /**
     * 关联数据ID（如课程ID、考试ID等）
     */
    private String relatedId;
    
    /**
     * 关联数据类型（如COURSE、EXAM等）
     */
    private String relatedType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 是否删除：0-未删除, 1-已删除
     */
    private Boolean deleted;
}
