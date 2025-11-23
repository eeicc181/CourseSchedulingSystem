package com.chq.coursearrange.entity.response;

import lombok.Data;
import java.util.List;

/**
 * 课程冲突检测响应对象
 * @author CHQ
 */
@Data
public class ConflictDetectionVO {
    
    /**
     * 冲突ID
     */
    private String conflictId;
    
    /**
     * 冲突类型: TIME-时间冲突, CLASSROOM-教室冲突, TEACHER-教师冲突
     */
    private String conflictType;
    
    /**
     * 冲突级别: HIGH-高, MEDIUM-中, LOW-低
     */
    private String conflictLevel;
    
    /**
     * 冲突描述
     */
    private String description;
    
    /**
     * 冲突时间
     */
    private String conflictTime;
    
    /**
     * 涉及的课程计划列表
     */
    private List<CoursePlanInfo> coursePlans;
    
    /**
     * 建议解决方案
     */
    private String suggestion;
    
    /**
     * 课程计划信息
     */
    @Data
    public static class CoursePlanInfo {
        /**
         * 课程计划ID
         */
        private Integer id;
        
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
         * 教室编号
         */
        private String classroomNo;
        
        /**
         * 教室名称
         */
        private String classroomName;
        
        /**
         * 上课时间
         */
        private String classTime;
        
        /**
         * 学期
         */
        private String semester;
    }
}
