package com.chq.coursearrange.entity.response;

import lombok.Data;
import java.util.List;

/**
 * 教室利用率统计响应对象
 * @author CHQ
 */
@Data
public class ClassroomUtilizationVO {
    
    /**
     * 教室ID
     */
    private Integer id;
    
    /**
     * 教室编号
     */
    private String classroomNo;
    
    /**
     * 教室名称
     */
    private String classroomName;
    
    /**
     * 所属教学楼编号
     */
    private String teachbuildNo;
    
    /**
     * 所属教学楼名称
     */
    private String teachBuildName;
    
    /**
     * 容量
     */
    private Integer capacity;
    
    /**
     * 利用率（百分比）
     */
    private Double utilizationRate;
    
    /**
     * 已使用时长（小时）
     */
    private Integer usedHours;
    
    /**
     * 空闲时长（小时）
     */
    private Integer availableHours;
    
    /**
     * 当前状态: free-空闲, occupied-使用中, maintenance-维护中
     */
    private String status;
    
    /**
     * 时间段使用情况列表
     */
    private List<TimeSlotVO> timeSlots;
    
    /**
     * 设备信息
     */
    private String equipment;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 时间段VO
     */
    @Data
    public static class TimeSlotVO {
        /**
         * 状态: used-已占用, free-空闲
         */
        private String status;
        
        /**
         * 信息描述
         */
        private String info;
        
        /**
         * 课程名称（如果已占用）
         */
        private String courseName;
    }
}
