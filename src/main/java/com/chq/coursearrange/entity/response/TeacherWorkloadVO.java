package com.chq.coursearrange.entity.response;

import lombok.Data;
import java.util.List;

/**
 * 教师工作量统计响应对象
 * @author CHQ
 */
@Data
public class TeacherWorkloadVO {
    
    /**
     * 教师ID
     */
    private Integer id;
    
    /**
     * 教师编号
     */
    private String teacherNo;
    
    /**
     * 教师姓名
     */
    private String teacherName;
    
    /**
     * 所属部门
     */
    private String department;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 总课时数
     */
    private Integer totalHours;
    
    /**
     * 周课时数
     */
    private Integer weeklyHours;
    
    /**
     * 授课班级数
     */
    private Integer classCount;
    
    /**
     * 授课学生数
     */
    private Integer studentCount;
    
    /**
     * 课程列表
     */
    private List<CourseDetail> courses;
    
    /**
     * 工作负荷等级: low-轻, normal-正常, high-重, overload-超负荷
     */
    private String workloadLevel;
    
    /**
     * 工作负荷百分比
     */
    private Double workloadPercentage;
    
    /**
     * 课程详情
     */
    @Data
    public static class CourseDetail {
        /**
         * 课程编号
         */
        private String courseNo;
        
        /**
         * 课程名称
         */
        private String courseName;
        
        /**
         * 班级名称
         */
        private String className;
        
        /**
         * 周学时
         */
        private Integer weeklyHours;
        
        /**
         * 学生人数
         */
        private Integer studentCount;
        
        /**
         * 上课时间
         */
        private String classTime;
    }
}
