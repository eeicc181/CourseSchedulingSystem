package com.chq.coursearrange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chq.coursearrange.entity.CourseSelection;

import java.util.List;
import java.util.Map;

/**
 * 选课服务接口
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public interface CourseSelectionService extends IService<CourseSelection> {
    
    /**
     * 学生选课
     * @param studentNo 学号
     * @param courseNo 课程编号
     * @param semester 学期
     * @param priority 优先级
     * @return 是否成功
     */
    boolean selectCourse(String studentNo, String courseNo, String semester, Integer priority);
    
    /**
     * 取消选课
     * @param selectionId 选课记录ID
     * @return 是否成功
     */
    boolean cancelSelection(Integer selectionId);
    
    /**
     * 获取学生选课列表
     * @param studentNo 学号
     * @param semester 学期
     * @return 选课列表
     */
    List<CourseSelection> getStudentSelections(String studentNo, String semester);
    
    /**
     * 获取课程选课统计
     * @param courseNo 课程编号
     * @param semester 学期
     * @return 统计信息
     */
    Map<String, Object> getCourseSelectionStats(String courseNo, String semester);
}
