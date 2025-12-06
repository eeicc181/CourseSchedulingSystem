package com.chq.coursearrange.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chq.coursearrange.entity.Score;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩 Dao
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Mapper
public interface ScoreDao extends BaseMapper<Score> {
}
