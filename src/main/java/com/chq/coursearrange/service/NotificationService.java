package com.chq.coursearrange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chq.coursearrange.entity.Notification;

import java.util.List;

/**
 * 消息通知服务接口
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public interface NotificationService extends IService<Notification> {
    
    /**
     * 发送系统通知
     * @param title 标题
     * @param content 内容
     * @param receiverType 接收者类型
     * @param receiverId 接收者ID（可选）
     */
    void sendSystemNotification(String title, String content, String receiverType, String receiverId);
    
    /**
     * 发送排课完成通知
     * @param semester 学期
     */
    void sendScheduleCompleteNotification(String semester);
    
    /**
     * 发送课表变更通知
     * @param receiverId 接收者ID
     * @param changeInfo 变更信息
     */
    void sendScheduleChangeNotification(String receiverId, String changeInfo);
    
    /**
     * 获取用户未读消息列表
     * @param receiverId 接收者ID
     * @return 未读消息列表
     */
    List<Notification> getUnreadNotifications(String receiverId);
    
    /**
     * 标记消息为已读
     * @param notificationId 消息ID
     */
    void markAsRead(Integer notificationId);
    
    /**
     * 批量标记为已读
     * @param receiverId 接收者ID
     */
    void markAllAsRead(String receiverId);
    
    /**
     * 获取用户未读消息数量
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    int getUnreadCount(String receiverId);
}
