package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.CacheConstants;
import com.chq.coursearrange.dao.CoursePlanDao;
import com.chq.coursearrange.entity.CoursePlan;
import com.chq.coursearrange.service.CoursePlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程计划服务实现（带缓存）
 * 
 * @author CHQ
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
public class CoursePlanServiceImpl extends ServiceImpl<CoursePlanDao, CoursePlan> implements CoursePlanService {

    /**
     * 根据学期和班级查询课表（带缓存）
     * 缓存key: coursePlan::semester:classNo
     */
    @Cacheable(value = CacheConstants.CACHE_COURSE_PLAN, key = "#semester + ':' + #classNo")
    public List<CoursePlan> getCoursePlanByClass(String semester, String classNo) {
        return this.list(new QueryWrapper<CoursePlan>()
                .eq("semester", semester)
                .eq("class_no", classNo));
    }
    
    /**
     * 根据学期和教师查询课表（带缓存）
     * 缓存key: coursePlan::semester:teacherNo
     */
    @Cacheable(value = CacheConstants.CACHE_COURSE_PLAN, key = "#semester + ':teacher:' + #teacherNo")
    public List<CoursePlan> getCoursePlanByTeacher(String semester, String teacherNo) {
        return this.list(new QueryWrapper<CoursePlan>()
                .eq("semester", semester)
                .eq("teacher_no", teacherNo));
    }
    
    /**
     * 根据学期查询所有课表（带缓存）
     * 缓存key: coursePlan::semester
     */
    @Cacheable(value = CacheConstants.CACHE_COURSE_PLAN, key = "#semester")
    public List<CoursePlan> getCoursePlanBySemester(String semester) {
        return this.list(new QueryWrapper<CoursePlan>()
                .eq("semester", semester));
    }
    
    /**
     * 清除所有课表缓存
     * 在排课完成后调用
     */
    @CacheEvict(value = CacheConstants.CACHE_COURSE_PLAN, allEntries = true)
    public void clearAllCache() {
        // 清除缓存
    }
}
