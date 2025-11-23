package com.chq.coursearrange.entity.response;

import lombok.Data;
import java.util.List;

/**
 * 智能排课建议响应对象
 * @author CHQ
 */
@Data
public class ScheduleSuggestionVO {
    
    /**
     * 建议ID
     */
    private String suggestionId;
    
    /**
     * 课程编号
     */
    private String courseNo;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教师编号
     */
    private String teacherNo;
    
    /**
     * 教师姓名
     */
    private String teacherName;
    
    /**
     * 班级编号
     */
    private String classNo;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 建议的时间段列表
     */
    private List<TimeSlotSuggestion> suggestedTimeSlots;
    
    /**
     * 建议的教室列表
     */
    private List<ClassroomSuggestion> suggestedClassrooms;
    
    /**
     * 优先级: HIGH-高, MEDIUM-中, LOW-低
     */
    private String priority;
    
    /**
     * 建议原因
     */
    private String reason;
    
    /**
     * 时间段建议
     */
    @Data
    public static class TimeSlotSuggestion {
        /**
         * 时间段描述
         */
        private String timeSlot;
        
        /**
         * 星期几
         */
        private Integer dayOfWeek;
        
        /**
         * 节次
         */
        private String period;
        
        /**
         * 适合度评分 (0-100)
         */
        private Integer score;
        
        /**
         * 评分原因
         */
        private String scoreReason;
        
        /**
         * 是否推荐
         */
        private Boolean recommended;
    }
    
    /**
     * 教室建议
     */
    @Data
    public static class ClassroomSuggestion {
        /**
         * 教室编号
         */
        private String classroomNo;
        
        /**
         * 教室名称
         */
        private String classroomName;
        
        /**
         * 教学楼名称
         */
        private String buildingName;
        
        /**
         * 教室容量
         */
        private Integer capacity;
        
        /**
         * 适合度评分 (0-100)
         */
        private Integer score;
        
        /**
         * 评分原因
         */
        private String scoreReason;
        
        /**
         * 是否推荐
         */
        private Boolean recommended;
    }
}
