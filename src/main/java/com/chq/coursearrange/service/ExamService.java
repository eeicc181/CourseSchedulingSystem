package com.chq.coursearrange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chq.coursearrange.entity.Exam;

import java.util.List;
import java.util.Map;

/**
 * 考试服务接口
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public interface ExamService extends IService<Exam> {
    
    /**
     * 创建考试安排
     * @param exam 考试信息
     * @return 是否成功
     */
    boolean createExam(Exam exam);
    
    /**
     * 自动分配考场
     * @param examId 考试ID
     * @return 是否成功
     */
    boolean autoAssignClassroom(Integer examId);
    
    /**
     * 自动分配监考教师
     * @param examId 考试ID
     * @param count 监考教师数量
     * @return 是否成功
     */
    boolean autoAssignInvigilators(Integer examId, Integer count);
    
    /**
     * 生成准考证号
     * @param examId 考试ID
     * @return 是否成功
     */
    boolean generateAdmissionNumbers(Integer examId);
    
    /**
     * 获取考试列表
     * @param semester 学期
     * @param status 状态
     * @return 考试列表
     */
    List<Exam> getExamList(String semester, String status);
    
    /**
     * 获取考试统计
     * @param semester 学期
     * @return 统计信息
     */
    Map<String, Object> getExamStatistics(String semester);
}
