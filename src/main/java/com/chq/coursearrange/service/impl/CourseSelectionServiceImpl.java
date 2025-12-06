package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chq.coursearrange.dao.CourseSelectionDao;
import com.chq.coursearrange.entity.CourseSelection;
import com.chq.coursearrange.service.CourseSelectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课服务实现
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
@Slf4j
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionDao, CourseSelection> 
        implements CourseSelectionService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectCourse(String studentNo, String courseNo, String semester, Integer priority) {
        try {
            // 检查是否已选过该课程
            QueryWrapper<CourseSelection> wrapper = new QueryWrapper<>();
            wrapper.eq("student_no", studentNo)
                   .eq("course_no", courseNo)
                   .eq("semester", semester)
                   .eq("deleted", false);
            
            CourseSelection existing = this.getOne(wrapper);
            if (existing != null) {
                log.warn("学生已选过该课程: studentNo={}, courseNo={}", studentNo, courseNo);
                return false;
            }
            
            // 创建选课记录
            CourseSelection selection = new CourseSelection();
            selection.setStudentNo(studentNo);
            selection.setCourseNo(courseNo);
            selection.setSemester(semester);
            selection.setPriority(priority != null ? priority : 1);
            selection.setStatus("PENDING");
            selection.setSelectionTime(LocalDateTime.now());
            selection.setDeleted(false);
            
            boolean result = this.save(selection);
            if (result) {
                log.info("选课成功: studentNo={}, courseNo={}", studentNo, courseNo);
            }
            return result;
        } catch (Exception e) {
            log.error("选课失败", e);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSelection(Integer selectionId) {
        try {
            CourseSelection selection = this.getById(selectionId);
            if (selection == null) {
                log.warn("选课记录不存在: id={}", selectionId);
                return false;
            }
            
            if ("CONFIRMED".equals(selection.getStatus())) {
                log.warn("已确认的选课不能取消: id={}", selectionId);
                return false;
            }
            
            selection.setStatus("CANCELLED");
            selection.setDeleted(true);
            
            boolean result = this.updateById(selection);
            if (result) {
                log.info("取消选课成功: id={}", selectionId);
            }
            return result;
        } catch (Exception e) {
            log.error("取消选课失败", e);
            throw e;
        }
    }
    
    @Override
    public List<CourseSelection> getStudentSelections(String studentNo, String semester) {
        QueryWrapper<CourseSelection> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", studentNo)
               .eq("semester", semester)
               .eq("deleted", false)
               .orderByAsc("priority");
        
        return this.list(wrapper);
    }
    
    @Override
    public Map<String, Object> getCourseSelectionStats(String courseNo, String semester) {
        Map<String, Object> stats = new HashMap<>();
        
        QueryWrapper<CourseSelection> wrapper = new QueryWrapper<>();
        wrapper.eq("course_no", courseNo)
               .eq("semester", semester)
               .eq("deleted", false);
        
        List<CourseSelection> selections = this.list(wrapper);
        
        int totalSelections = selections.size();
        int confirmedCount = 0;
        int pendingCount = 0;
        int rejectedCount = 0;
        
        for (CourseSelection selection : selections) {
            switch (selection.getStatus()) {
                case "CONFIRMED":
                    confirmedCount++;
                    break;
                case "PENDING":
                    pendingCount++;
                    break;
                case "REJECTED":
                    rejectedCount++;
                    break;
            }
        }
        
        stats.put("totalSelections", totalSelections);
        stats.put("confirmedCount", confirmedCount);
        stats.put("pendingCount", pendingCount);
        stats.put("rejectedCount", rejectedCount);
        
        return stats;
    }
}
