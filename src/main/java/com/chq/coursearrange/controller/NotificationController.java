package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chq.coursearrange.common.NotificationConstants;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.Notification;
import com.chq.coursearrange.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/notification")
@Api(tags = "消息通知管理")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取用户未读消息列表
     */
    @GetMapping("/unread/{receiverId}")
    @ApiOperation("获取未读消息列表")
    public ServerResponse getUnreadNotifications(
            @ApiParam("接收者ID") @PathVariable String receiverId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(receiverId);
            return ServerResponse.ofSuccess(notifications);
        } catch (Exception e) {
            return ServerResponse.ofError("获取未读消息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户所有消息（分页）
     */
    @GetMapping("/list/{receiverId}")
    @ApiOperation("获取消息列表（分页）")
    public ServerResponse getNotifications(
            @ApiParam("接收者ID") @PathVariable String receiverId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            Page<Notification> pages = new Page<>(page, limit);
            QueryWrapper<Notification> wrapper = new QueryWrapper<>();
            wrapper.eq("deleted", false)
                   .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                              .or()
                              .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                              .eq("receiver_id", receiverId))
                   .orderByDesc("create_time");
            
            IPage<Notification> ipage = notificationService.page(pages, wrapper);
            return ServerResponse.ofSuccess(ipage);
        } catch (Exception e) {
            return ServerResponse.ofError("获取消息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count/{receiverId}")
    @ApiOperation("获取未读消息数量")
    public ServerResponse getUnreadCount(
            @ApiParam("接收者ID") @PathVariable String receiverId) {
        try {
            int count = notificationService.getUnreadCount(receiverId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            return ServerResponse.ofSuccess(result);
        } catch (Exception e) {
            return ServerResponse.ofError("获取未读数量失败：" + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{notificationId}")
    @ApiOperation("标记消息为已读")
    public ServerResponse markAsRead(
            @ApiParam("消息ID") @PathVariable Integer notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ServerResponse.ofSuccess("标记成功");
        } catch (Exception e) {
            return ServerResponse.ofError("标记失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量标记为已读
     */
    @PutMapping("/read/all/{receiverId}")
    @ApiOperation("批量标记为已读")
    public ServerResponse markAllAsRead(
            @ApiParam("接收者ID") @PathVariable String receiverId) {
        try {
            notificationService.markAllAsRead(receiverId);
            return ServerResponse.ofSuccess("全部标记成功");
        } catch (Exception e) {
            return ServerResponse.ofError("批量标记失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除消息（软删除）
     */
    @DeleteMapping("/delete/{notificationId}")
    @ApiOperation("删除消息")
    public ServerResponse deleteNotification(
            @ApiParam("消息ID") @PathVariable Integer notificationId) {
        try {
            Notification notification = notificationService.getById(notificationId);
            if (notification != null) {
                notification.setDeleted(true);
                notificationService.updateById(notification);
                return ServerResponse.ofSuccess("删除成功");
            }
            return ServerResponse.ofError("消息不存在");
        } catch (Exception e) {
            return ServerResponse.ofError("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送系统通知（管理员）
     */
    @PostMapping("/send/system")
    @ApiOperation("发送系统通知")
    public ServerResponse sendSystemNotification(
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content,
            @ApiParam("接收者类型") @RequestParam String receiverType,
            @ApiParam("接收者ID（可选）") @RequestParam(required = false) String receiverId) {
        try {
            notificationService.sendSystemNotification(title, content, receiverType, receiverId);
            return ServerResponse.ofSuccess("发送成功");
        } catch (Exception e) {
            return ServerResponse.ofError("发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取消息详情
     */
    @GetMapping("/detail/{notificationId}")
    @ApiOperation("获取消息详情")
    public ServerResponse getNotificationDetail(
            @ApiParam("消息ID") @PathVariable Integer notificationId) {
        try {
            Notification notification = notificationService.getById(notificationId);
            if (notification != null && !notification.getDeleted()) {
                // 自动标记为已读
                if (notification.getIsRead() == NotificationConstants.ReadStatus.UNREAD) {
                    notificationService.markAsRead(notificationId);
                }
                return ServerResponse.ofSuccess(notification);
            }
            return ServerResponse.ofError("消息不存在");
        } catch (Exception e) {
            return ServerResponse.ofError("获取详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取消息统计信息
     */
    @GetMapping("/stats/{receiverId}")
    @ApiOperation("获取消息统计")
    public ServerResponse getNotificationStats(
            @ApiParam("接收者ID") @PathVariable String receiverId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 未读数量
            int unreadCount = notificationService.getUnreadCount(receiverId);
            stats.put("unreadCount", unreadCount);
            
            // 总消息数
            QueryWrapper<Notification> wrapper = new QueryWrapper<>();
            wrapper.eq("deleted", false)
                   .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                              .or()
                              .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                              .eq("receiver_id", receiverId));
            int totalCount = notificationService.count(wrapper);
            stats.put("totalCount", totalCount);
            
            // 按类型统计
            QueryWrapper<Notification> systemWrapper = new QueryWrapper<>();
            systemWrapper.eq("type", NotificationConstants.Type.SYSTEM)
                        .eq("deleted", false)
                        .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                                   .or()
                                   .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                                   .eq("receiver_id", receiverId));
            int systemCount = notificationService.count(systemWrapper);
            stats.put("systemCount", systemCount);
            
            QueryWrapper<Notification> scheduleWrapper = new QueryWrapper<>();
            scheduleWrapper.eq("type", NotificationConstants.Type.SCHEDULE)
                          .eq("deleted", false)
                          .and(w -> w.eq("receiver_type", NotificationConstants.ReceiverType.ALL)
                                     .or()
                                     .eq("receiver_type", NotificationConstants.ReceiverType.USER)
                                     .eq("receiver_id", receiverId));
            int scheduleCount = notificationService.count(scheduleWrapper);
            stats.put("scheduleCount", scheduleCount);
            
            return ServerResponse.ofSuccess(stats);
        } catch (Exception e) {
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
}
