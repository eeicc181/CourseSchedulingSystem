package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.entity.response.ConflictDetectionVO;
import com.chq.coursearrange.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 课程冲突检测控制器
 * @author CHQ
 */
@RestController
@RequestMapping("/conflict")
public class ConflictDetectionController {

    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CourseInfoService courseInfoService;
    
    @Autowired
    private ClassroomService classroomService;

    /**
     * 检测所有冲突
     * @param semester 学期
     * @return 冲突列表
     */
    @GetMapping("/detect")
    public ServerResponse detectConflicts(@RequestParam(required = false) String semester) {
        try {
            List<ConflictDetectionVO> conflicts = new ArrayList<>();
            
            // 获取课程计划
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 检测教师冲突
            conflicts.addAll(detectTeacherConflicts(coursePlans));
            
            // 检测教室冲突
            conflicts.addAll(detectClassroomConflicts(coursePlans));
            
            // 检测时间冲突（同一班级）
            conflicts.addAll(detectClassTimeConflicts(coursePlans));
            
            return ServerResponse.ofSuccess(conflicts);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("检测冲突失败：" + e.getMessage());
        }
    }
    
    /**
     * 检测教师冲突
     */
    private List<ConflictDetectionVO> detectTeacherConflicts(List<CoursePlan> coursePlans) {
        List<ConflictDetectionVO> conflicts = new ArrayList<>();
        
        // 按教师和时间分组
        Map<String, List<CoursePlan>> teacherTimeMap = new HashMap<>();
        
        for (CoursePlan plan : coursePlans) {
            String key = plan.getTeacherNo() + "_" + plan.getClassTime();
            teacherTimeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(plan);
        }
        
        // 查找冲突
        for (Map.Entry<String, List<CoursePlan>> entry : teacherTimeMap.entrySet()) {
            List<CoursePlan> plans = entry.getValue();
            if (plans.size() > 1) {
                ConflictDetectionVO conflict = new ConflictDetectionVO();
                conflict.setConflictId(UUID.randomUUID().toString());
                conflict.setConflictType("TEACHER");
                conflict.setConflictLevel("HIGH");
                conflict.setConflictTime(plans.get(0).getClassTime());
                
                String teacherName = getTeacherName(plans.get(0).getTeacherNo());
                conflict.setDescription("教师 " + teacherName + " 在 " + plans.get(0).getClassTime() + " 有 " + plans.size() + " 个课程安排");
                conflict.setSuggestion("调整其中 " + (plans.size() - 1) + " 个课程的上课时间");
                
                List<ConflictDetectionVO.CoursePlanInfo> planInfos = new ArrayList<>();
                for (CoursePlan plan : plans) {
                    planInfos.add(convertToCoursePlanInfo(plan));
                }
                conflict.setCoursePlans(planInfos);
                
                conflicts.add(conflict);
            }
        }
        
        return conflicts;
    }
    
    /**
     * 检测教室冲突
     */
    private List<ConflictDetectionVO> detectClassroomConflicts(List<CoursePlan> coursePlans) {
        List<ConflictDetectionVO> conflicts = new ArrayList<>();
        
        // 按教室和时间分组
        Map<String, List<CoursePlan>> classroomTimeMap = new HashMap<>();
        
        for (CoursePlan plan : coursePlans) {
            if (plan.getClassroomNo() != null && !plan.getClassroomNo().isEmpty()) {
                String key = plan.getClassroomNo() + "_" + plan.getClassTime();
                classroomTimeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(plan);
            }
        }
        
        // 查找冲突
        for (Map.Entry<String, List<CoursePlan>> entry : classroomTimeMap.entrySet()) {
            List<CoursePlan> plans = entry.getValue();
            if (plans.size() > 1) {
                ConflictDetectionVO conflict = new ConflictDetectionVO();
                conflict.setConflictId(UUID.randomUUID().toString());
                conflict.setConflictType("CLASSROOM");
                conflict.setConflictLevel("HIGH");
                conflict.setConflictTime(plans.get(0).getClassTime());
                
                String classroomName = getClassroomName(plans.get(0).getClassroomNo());
                conflict.setDescription("教室 " + classroomName + " 在 " + plans.get(0).getClassTime() + " 被 " + plans.size() + " 个课程占用");
                conflict.setSuggestion("为其中 " + (plans.size() - 1) + " 个课程更换教室或调整时间");
                
                List<ConflictDetectionVO.CoursePlanInfo> planInfos = new ArrayList<>();
                for (CoursePlan plan : plans) {
                    planInfos.add(convertToCoursePlanInfo(plan));
                }
                conflict.setCoursePlans(planInfos);
                
                conflicts.add(conflict);
            }
        }
        
        return conflicts;
    }
    
    /**
     * 检测班级时间冲突
     */
    private List<ConflictDetectionVO> detectClassTimeConflicts(List<CoursePlan> coursePlans) {
        List<ConflictDetectionVO> conflicts = new ArrayList<>();
        
        // 按班级和时间分组
        Map<String, List<CoursePlan>> classTimeMap = new HashMap<>();
        
        for (CoursePlan plan : coursePlans) {
            String key = plan.getClassNo() + "_" + plan.getClassTime();
            classTimeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(plan);
        }
        
        // 查找冲突
        for (Map.Entry<String, List<CoursePlan>> entry : classTimeMap.entrySet()) {
            List<CoursePlan> plans = entry.getValue();
            if (plans.size() > 1) {
                ConflictDetectionVO conflict = new ConflictDetectionVO();
                conflict.setConflictId(UUID.randomUUID().toString());
                conflict.setConflictType("TIME");
                conflict.setConflictLevel("MEDIUM");
                conflict.setConflictTime(plans.get(0).getClassTime());
                
                conflict.setDescription("班级 " + plans.get(0).getClassNo() + " 在 " + plans.get(0).getClassTime() + " 有 " + plans.size() + " 个课程");
                conflict.setSuggestion("调整其中 " + (plans.size() - 1) + " 个课程的上课时间");
                
                List<ConflictDetectionVO.CoursePlanInfo> planInfos = new ArrayList<>();
                for (CoursePlan plan : plans) {
                    planInfos.add(convertToCoursePlanInfo(plan));
                }
                conflict.setCoursePlans(planInfos);
                
                conflicts.add(conflict);
            }
        }
        
        return conflicts;
    }
    
    /**
     * 获取冲突统计
     */
    @GetMapping("/summary")
    public ServerResponse getConflictSummary(@RequestParam(required = false) String semester) {
        try {
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            List<ConflictDetectionVO> teacherConflicts = detectTeacherConflicts(coursePlans);
            List<ConflictDetectionVO> classroomConflicts = detectClassroomConflicts(coursePlans);
            List<ConflictDetectionVO> timeConflicts = detectClassTimeConflicts(coursePlans);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalConflicts", teacherConflicts.size() + classroomConflicts.size() + timeConflicts.size());
            summary.put("teacherConflicts", teacherConflicts.size());
            summary.put("classroomConflicts", classroomConflicts.size());
            summary.put("timeConflicts", timeConflicts.size());
            summary.put("highLevelConflicts", teacherConflicts.size() + classroomConflicts.size());
            summary.put("mediumLevelConflicts", timeConflicts.size());
            
            return ServerResponse.ofSuccess(summary);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换为课程计划信息
     */
    private ConflictDetectionVO.CoursePlanInfo convertToCoursePlanInfo(CoursePlan plan) {
        ConflictDetectionVO.CoursePlanInfo info = new ConflictDetectionVO.CoursePlanInfo();
        info.setId(plan.getId());
        info.setCourseNo(plan.getCourseNo());
        info.setTeacherNo(plan.getTeacherNo());
        info.setClassNo(plan.getClassNo());
        info.setClassroomNo(plan.getClassroomNo());
        info.setClassTime(plan.getClassTime());
        info.setSemester(plan.getSemester());
        
        // 获取课程名称
        CourseInfo courseInfo = courseInfoService.getOne(
                new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
        info.setCourseName(courseInfo != null ? courseInfo.getCourseName() : "未知课程");
        
        // 获取教师姓名
        info.setTeacherName(getTeacherName(plan.getTeacherNo()));
        
        // 获取教室名称
        info.setClassroomName(getClassroomName(plan.getClassroomNo()));
        
        // 班级名称
        info.setClassName(plan.getClassNo());
        
        return info;
    }
    
    /**
     * 获取教师姓名
     */
    private String getTeacherName(String teacherNo) {
        if (teacherNo == null || teacherNo.isEmpty()) {
            return "未分配";
        }
        Teacher teacher = teacherService.getOne(
                new QueryWrapper<Teacher>().eq("teacher_no", teacherNo));
        return teacher != null ? teacher.getRealname() : teacherNo;
    }
    
    /**
     * 获取教室名称
     */
    private String getClassroomName(String classroomNo) {
        if (classroomNo == null || classroomNo.isEmpty()) {
            return "未分配";
        }
        Classroom classroom = classroomService.getOne(
                new QueryWrapper<Classroom>().eq("classroom_no", classroomNo));
        return classroom != null ? classroom.getClassroomName() : classroomNo;
    }
}
