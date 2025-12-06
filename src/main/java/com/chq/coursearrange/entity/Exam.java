package com.chq.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试实体
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
@TableName("tb_exam")
public class Exam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 考试名称
     */
    private String examName;
    
    /**
     * 课程编号
     */
    private String courseNo;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 考试类型：MIDTERM-期中, FINAL-期末, MAKEUP-补考, RETAKE-重修
     */
    private String examType;
    
    /**
     * 考试时间
     */
    private LocalDateTime examTime;
    
    /**
     * 考试时长（分钟）
     */
    private Integer duration;
    
    /**
     * 考场编号
     */
    private String classroomNo;
    
    /**
     * 监考教师编号（多个用逗号分隔）
     */
    private String invigilators;
    
    /**
     * 参考班级（多个用逗号分隔）
     */
    private String classNos;
    
    /**
     * 考试状态：SCHEDULED-已安排, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
}
