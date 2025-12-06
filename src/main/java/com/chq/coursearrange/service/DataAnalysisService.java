package com.chq.coursearrange.service;

import java.util.Map;

/**
 * 数据分析服务接口
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public interface DataAnalysisService {
    
    /**
     * 获取系统概览数据
     * @param semester 学期
     * @return 概览数据
     */
    Map<String, Object> getSystemOverview(String semester);
    
    /**
     * 获取排课统计数据
     * @param semester 学期
     * @return 排课统计
     */
    Map<String, Object> getScheduleStatistics(String semester);
    
    /**
     * 获取教室利用率数据
     * @param semester 学期
     * @return 教室利用率
     */
    Map<String, Object> getClassroomUtilization(String semester);
    
    /**
     * 获取教师工作量统计
     * @param semester 学期
     * @return 教师工作量
     */
    Map<String, Object> getTeacherWorkload(String semester);
    
    /**
     * 获取课程时间分布
     * @param semester 学期
     * @return 时间分布
     */
    Map<String, Object> getCourseTimeDistribution(String semester);
    
    /**
     * 获取冲突统计
     * @param semester 学期
     * @return 冲突统计
     */
    Map<String, Object> getConflictStatistics(String semester);
}
