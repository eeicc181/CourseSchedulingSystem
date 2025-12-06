package com.chq.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 选课记录实体
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
@TableName("tb_course_selection")
public class CourseSelection implements Serializable {
    
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
     * 志愿优先级：1-第一志愿, 2-第二志愿, 3-第三志愿
     */
    private Integer priority;
    
    /**
     * 选课状态：PENDING-待处理, CONFIRMED-已确认, REJECTED-已拒绝, CANCELLED-已取消
     */
    private String status;
    
    /**
     * 选课时间
     */
    private LocalDateTime selectionTime;
    
    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
}
