package com.chq.coursearrange.common;

import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class ValidationUtil {
    
    private ValidationUtil() {
        // 工具类，隐藏构造函数
    }
    
    private static final Pattern STUDENT_NO_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern TEACHER_NO_PATTERN = Pattern.compile("^T\\d{3,}$");
    private static final Pattern COURSE_NO_PATTERN = Pattern.compile("^[A-Z]{2,}\\d{3}$");
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^\\d{4}-\\d{4}-[12]$");
    
    /**
     * 验证学号格式
     */
    public static boolean isValidStudentNo(String studentNo) {
        return studentNo != null && STUDENT_NO_PATTERN.matcher(studentNo).matches();
    }
    
    /**
     * 验证教师编号格式
     */
    public static boolean isValidTeacherNo(String teacherNo) {
        return teacherNo != null && TEACHER_NO_PATTERN.matcher(teacherNo).matches();
    }
    
    /**
     * 验证课程编号格式
     */
    public static boolean isValidCourseNo(String courseNo) {
        return courseNo != null && COURSE_NO_PATTERN.matcher(courseNo).matches();
    }
    
    /**
     * 验证学期格式
     */
    public static boolean isValidSemester(String semester) {
        return semester != null && SEMESTER_PATTERN.matcher(semester).matches();
    }
    
    /**
     * 验证成绩范围
     */
    public static boolean isValidScore(Double score) {
        return score != null && score >= 0 && score <= 100;
    }
    
    /**
     * 验证绩点范围
     */
    public static boolean isValidGradePoint(Double gradePoint) {
        return gradePoint != null && gradePoint >= 0 && gradePoint <= 4.0;
    }
    
    /**
     * 验证字符串非空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
