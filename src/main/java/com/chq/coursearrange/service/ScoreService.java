package com.chq.coursearrange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chq.coursearrange.entity.Score;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public interface ScoreService extends IService<Score> {
    
    /**
     * 录入成绩
     * @param score 成绩信息
     * @return 是否成功
     */
    boolean inputScore(Score score);
    
    /**
     * 批量录入成绩
     * @param scores 成绩列表
     * @return 是否成功
     */
    boolean batchInputScore(List<Score> scores);
    
    /**
     * 计算总评成绩
     * @param score 成绩对象
     */
    void calculateTotalScore(Score score);
    
    /**
     * 获取学生成绩列表
     * @param studentNo 学号
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> getStudentScores(String studentNo, String semester);
    
    /**
     * 获取课程成绩列表
     * @param courseNo 课程编号
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> getCourseScores(String courseNo, String semester);
    
    /**
     * 获取成绩统计
     * @param courseNo 课程编号
     * @param semester 学期
     * @return 统计信息
     */
    Map<String, Object> getScoreStatistics(String courseNo, String semester);
    
    /**
     * 生成成绩单
     * @param studentNo 学号
     * @param semester 学期
     * @return 成绩单数据
     */
    Map<String, Object> generateTranscript(String studentNo, String semester);
}
