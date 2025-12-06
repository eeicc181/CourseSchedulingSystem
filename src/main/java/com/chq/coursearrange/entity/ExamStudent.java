package com.chq.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 考试学生关联实体
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
@TableName("tb_exam_student")
public class ExamStudent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 考试ID
     */
    private Integer examId;
    
    /**
     * 学生学号
     */
    private String studentNo;
    
    /**
     * 座位号
     */
    private String seatNo;
    
    /**
     * 准考证号
     */
    private String admissionNo;
    
    /**
     * 是否缺考
     */
    private Boolean absent;
    
    /**
     * 成绩
     */
    private Double score;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
}
