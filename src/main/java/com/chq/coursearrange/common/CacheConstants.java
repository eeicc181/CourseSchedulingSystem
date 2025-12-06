package com.chq.coursearrange.common;

/**
 * 缓存常量
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class CacheConstants {
    
    /**
     * 课程计划缓存
     * 缓存时间：1小时
     */
    public static final String CACHE_COURSE_PLAN = "coursePlan";
    
    /**
     * 教师列表缓存
     * 缓存时间：30分钟
     */
    public static final String CACHE_TEACHER_LIST = "teacherList";
    
    /**
     * 学生列表缓存
     * 缓存时间：30分钟
     */
    public static final String CACHE_STUDENT_LIST = "studentList";
    
    /**
     * 课程信息缓存
     * 缓存时间：1小时
     */
    public static final String CACHE_COURSE_INFO = "courseInfo";
    
    /**
     * 教室信息缓存
     * 缓存时间：1小时
     */
    public static final String CACHE_CLASSROOM = "classroom";
    
    /**
     * 班级信息缓存
     * 缓存时间：1小时
     */
    public static final String CACHE_CLASS_INFO = "classInfo";
    
    /**
     * 缓存过期时间（秒）
     */
    public static class TTL {
        public static final long SHORT = 5 * 60;        // 5分钟
        public static final long MEDIUM = 30 * 60;      // 30分钟
        public static final long LONG = 60 * 60;        // 1小时
        public static final long VERY_LONG = 24 * 60 * 60;  // 24小时
    }
}
