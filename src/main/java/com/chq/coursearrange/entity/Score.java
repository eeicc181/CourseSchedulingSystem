package com.chq.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成绩实体
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
@TableName("tb_score")
public class Score implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 学生学号
     */
    private String studentNo;
    
    /**
     * 课程编号
     */
    private String courseNo;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 考试ID（关联考试表）
     */
    private Integer examId;
    
    /**
     * 平时成绩
     */
    private Double usualScore;
    
    /**
     * 期中成绩
     */
    private Double midtermScore;
    
    /**
     * 期末成绩
     */
    private Double finalScore;
    
    /**
     * 总评成绩
     */
    private Double totalScore;
    
    /**
     * 绩点
     */
    private Double gradePoint;
    
    /**
     * 等级：A/B/C/D/F
     */
    private String grade;
    
    /**
     * 是否通过
     */
    private Boolean passed;
    
    /**
     * 录入教师
     */
    private String teacherNo;
    
    /**
     * 录入时间
     */
    private LocalDateTime inputTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
}
