package com.chq.coursearrange.dao;

import com.chq.coursearrange.entity.ClassTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author CHQ
 */
@Component
@Mapper
public interface ClassTaskDao extends BaseMapper<ClassTask> {
    List<ClassTask> selectBySemester(ClassTask classTask);
    @Select("SELECT distinct class_no FROM tb_class_task")
    List<String> selectClassNo();
    @Select("select distinct ${columnName} from tb_class_task")
    List<String> selectByColumnName(@Param("columnName") String columnName);
    @Select("select distinct grade_no from tb_class_task")
    List<String> selectByGradeNo();
    @Update("truncate tb_class_task")
    void clearClassTaskOld();
}
