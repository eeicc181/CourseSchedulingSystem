package com.chq.coursearrange.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chq.coursearrange.entity.ExamStudent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试学生关联 Dao
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Mapper
public interface ExamStudentDao extends BaseMapper<ExamStudent> {
}
