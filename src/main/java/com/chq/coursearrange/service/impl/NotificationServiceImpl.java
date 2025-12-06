package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chq.coursearrange.common.NotificationConstants;
import com.chq.coursearrange.dao.NotificationDao;
import com.chq.coursearrange.entity.Notification;
import com.chq.coursearrange.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知服务实现
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
@Slf4j
public class NotificationServiceImpl extends ServiceImpl<NotificationDao, Notification> 
        implements NotificationService {
    
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemNotification(String title, String content, String receiverType, String receiverId) {
        try {
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(NotificationConstants.Type.SYSTEM);
            notification.setReceiverType(receiverType);
            notification.setReceiverId(receiverId);
            notification.setPriority(NotificationConstants.Priority.MEDIUM);
            notification.setIsRead(NotificationConstants.ReadStatus.UNREAD);
            notification.setChannel(NotificationConstants.Channel.SITE);
            notification.setStatus(NotificationConstants.Status.SENT);
            notification.setCreateTime(LocalDateTime.now());
            notification.setSendTime(LocalDateTime.now());
            notification.setDeleted(false);
            
            this.save(notification);
            log.info("系统通知发送成功: {}", title);
        } catch (Exception e) {
            log.error("发送系统通知失败", e);
        }
    }
    
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void sendScheduleCompleteNotification(String semester) {
        try {
            String title = "排课完成通知";
            String content = String.format("%s 学期的课程安排已完成，请查看您的课表。", semester);
            
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(NotificationConstants.Type.SCHEDULE);
            notification.setReceiverType(NotificationConstants.ReceiverType.ALL);
            notification.setPriority(NotificationConstants.Priority.HIGH);
            notification.setIsRead(NotificationConstants.ReadStatus.UNREAD);
            notification.setChannel(NotificationConstants.Channel.SITE);
            notification.setStatus(NotificationConstants.Status.SENT);
            notification.setRelatedId(semester);
            notification.setRelatedType("SEMESTER");
            notification.setCreateTime(LocalDateTime.now());
            notification.setSendTime(LocalDateTime.now());
            notification.setDeleted(false);
            
            this.save(notification);
            log.info("排课完成通知发送成功: {}", semester);
        } catch (Exception e) {
            log.error("发送排课完成通知失败", e);
        }
    }
    
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void sendScheduleChangeNotification(String receiverId, String changeInfo) {
        try {
            String title = "课表变更通知";
            String content = String.format("您的课表发生变更：%s，请及时查看。", changeInfo);
            
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(NotificationConstants.Type.CHANGE);
            notification.setReceiverType(NotificationConstants.ReceiverType.USER);
            notification.setReceiverId(receiverId);
            notification.setPriority(NotificationConstants.Priority.HIGH);
            notification.setIsRead(NotificationConstants.ReadStatus.UNREAD);
            notification.setChannel(NotificationConstants.Channel.SITE);
            notification.setStatus(NotificationConstants.Status.SENT);
            notification.setCreateTime(LocalDateTime.now());
            notification.setSendTime(LocalDateTime.now());
            notification.setDeleted(false);
            
            this.save(notification);
            log.info("课表变更通知发送成功: receiverId={}", receiverId);
        } catch (Exception e) {
            log.error("发送课表变更通知失败", e);
        }
    }
    
    @Override
    public List<Notification> getUnreadNotifications(String receiverId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("is_read", NotificationConstants.ReadStatus.UNREAD)
               .eq("deleted", false)
               .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                          .or()
                          .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                          .eq("receiver_id", receiverId))
               .orderByDesc("create_time");
        
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Integer notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification != null && notification.getIsRead() == NotificationConstants.ReadStatus.UNREAD) {
            notification.setIsRead(NotificationConstants.ReadStatus.READ);
            notification.setReadTime(LocalDateTime.now());
            this.updateById(notification);
            log.info("消息已标记为已读: id={}", notificationId);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(String receiverId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("is_read", NotificationConstants.ReadStatus.UNREAD)
               .eq("deleted", false)
               .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                          .or()
                          .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                          .eq("receiver_id", receiverId));
        
        List<Notification> notifications = this.list(wrapper);
        for (Notification notification : notifications) {
            notification.setIsRead(NotificationConstants.ReadStatus.READ);
            notification.setReadTime(LocalDateTime.now());
        }
        
        this.updateBatchById(notifications);
        log.info("所有消息已标记为已读: receiverId={}", receiverId);
    }
    
    @Override
    public int getUnreadCount(String receiverId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("is_read", NotificationConstants.ReadStatus.UNREAD)
               .eq("deleted", false)
               .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                          .or()
                          .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                          .eq("receiver_id", receiverId));
        
        return this.count(wrapper);
    }
}
