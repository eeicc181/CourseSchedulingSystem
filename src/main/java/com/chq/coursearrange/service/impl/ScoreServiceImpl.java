package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chq.coursearrange.dao.ScoreDao;
import com.chq.coursearrange.entity.Score;
import com.chq.coursearrange.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 成绩服务实现
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
@Slf4j
public class ScoreServiceImpl extends ServiceImpl<ScoreDao, Score> implements ScoreService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inputScore(Score score) {
        try {
            // 计算总评成绩
            calculateTotalScore(score);
            
            score.setInputTime(LocalDateTime.now());
            score.setDeleted(false);
            
            boolean result = this.save(score);
            if (result) {
                log.info("录入成绩成功: 学号={}, 课程={}", score.getStudentNo(), score.getCourseNo());
            }
            return result;
        } catch (Exception e) {
            log.error("录入成绩失败", e);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInputScore(List<Score> scores) {
        try {
            for (Score score : scores) {
                calculateTotalScore(score);
                score.setInputTime(LocalDateTime.now());
                score.setDeleted(false);
            }
            
            boolean result = this.saveBatch(scores);
            if (result) {
                log.info("批量录入成绩成功，共{}条", scores.size());
            }
            return result;
        } catch (Exception e) {
            log.error("批量录入成绩失败", e);
            throw e;
        }
    }
    
    @Override
    public void calculateTotalScore(Score score) {
        // 默认权重：平时30%，期中20%，期末50%
        double usual = score.getUsualScore() != null ? score.getUsualScore() : 0;
        double midterm = score.getMidtermScore() != null ? score.getMidtermScore() : 0;
        double finalScore = score.getFinalScore() != null ? score.getFinalScore() : 0;
        
        double total = usual * 0.3 + midterm * 0.2 + finalScore * 0.5;
        score.setTotalScore(total);
        
        // 计算等级
        if (total >= 90) {
            score.setGrade("A");
            score.setGradePoint(4.0);
        } else if (total >= 80) {
            score.setGrade("B");
            score.setGradePoint(3.0);
        } else if (total >= 70) {
            score.setGrade("C");
            score.setGradePoint(2.0);
        } else if (total >= 60) {
            score.setGrade("D");
            score.setGradePoint(1.0);
        } else {
            score.setGrade("F");
            score.setGradePoint(0.0);
        }
        
        // 判断是否通过
        score.setPassed(total >= 60);
    }
    
    @Override
    public List<Score> getStudentScores(String studentNo, String semester) {
        QueryWrapper<Score> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", studentNo);
        
        if (semester != null && !semester.isEmpty()) {
            wrapper.eq("semester", semester);
        }
        
        wrapper.eq("deleted", false);
        wrapper.orderByDesc("input_time");
        
        return this.list(wrapper);
    }
    
    @Override
    public List<Score> getCourseScores(String courseNo, String semester) {
        QueryWrapper<Score> wrapper = new QueryWrapper<>();
        wrapper.eq("course_no", courseNo);
        
        if (semester != null && !semester.isEmpty()) {
            wrapper.eq("semester", semester);
        }
        
        wrapper.eq("deleted", false);
        wrapper.orderByDesc("total_score");
        
        return this.list(wrapper);
    }
    
    @Override
    public Map<String, Object> getScoreStatistics(String courseNo, String semester) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Score> scores = getCourseScores(courseNo, semester);
            
            if (scores.isEmpty()) {
                return stats;
            }
            
            int totalCount = scores.size();
            int passedCount = 0;
            double totalSum = 0;
            double maxScore = 0;
            double minScore = 100;
            
            for (Score score : scores) {
                double total = score.getTotalScore();
                totalSum += total;
                
                if (score.getPassed()) {
                    passedCount++;
                }
                
                if (total > maxScore) {
                    maxScore = total;
                }
                
                if (total < minScore) {
                    minScore = total;
                }
            }
            
            double avgScore = totalSum / totalCount;
            double passRate = (double) passedCount / totalCount * 100;
            
            stats.put("totalCount", totalCount);
            stats.put("passedCount", passedCount);
            stats.put("passRate", String.format("%.2f", passRate));
            stats.put("avgScore", String.format("%.2f", avgScore));
            stats.put("maxScore", maxScore);
            stats.put("minScore", minScore);
            
        } catch (Exception e) {
            log.error("获取成绩统计失败", e);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> generateTranscript(String studentNo, String semester) {
        Map<String, Object> transcript = new HashMap<>();
        
        try {
            List<Score> scores = getStudentScores(studentNo, semester);
            
            double totalGradePoint = 0;
            int totalCredits = 0; // 假设每门课3学分
            int passedCount = 0;
            
            for (Score score : scores) {
                totalGradePoint += score.getGradePoint() * 3;
                totalCredits += 3;
                
                if (score.getPassed()) {
                    passedCount++;
                }
            }
            
            double gpa = totalCredits > 0 ? totalGradePoint / totalCredits : 0;
            
            transcript.put("studentNo", studentNo);
            transcript.put("semester", semester);
            transcript.put("scores", scores);
            transcript.put("totalCourses", scores.size());
            transcript.put("passedCourses", passedCount);
            transcript.put("gpa", String.format("%.2f", gpa));
            
        } catch (Exception e) {
            log.error("生成成绩单失败", e);
        }
        
        return transcript;
    }
}
