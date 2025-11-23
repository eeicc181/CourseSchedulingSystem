package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.entity.response.ScheduleSuggestionVO;
import com.chq.coursearrange.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能排课建议控制器
 * @author CHQ
 */
@RestController
@RequestMapping("/schedule/suggestion")
public class ScheduleSuggestionController {

    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CourseInfoService courseInfoService;
    
    @Autowired
    private ClassTaskService classTaskService;

    /**
     * 获取排课建议
     * @param courseNo 课程编号
     * @param teacherNo 教师编号
     * @param classNo 班级编号
     * @param semester 学期
     * @return 排课建议列表
     */
    @GetMapping("/get")
    public ServerResponse getSuggestions(
            @RequestParam String courseNo,
            @RequestParam String teacherNo,
            @RequestParam String classNo,
            @RequestParam(required = false) String semester) {
        try {
            ScheduleSuggestionVO suggestion = new ScheduleSuggestionVO();
            suggestion.setSuggestionId(UUID.randomUUID().toString());
            suggestion.setCourseNo(courseNo);
            suggestion.setTeacherNo(teacherNo);
            suggestion.setClassNo(classNo);
            
            // 获取课程信息
            CourseInfo courseInfo = courseInfoService.getOne(
                    new QueryWrapper<CourseInfo>().eq("course_no", courseNo));
            suggestion.setCourseName(courseInfo != null ? courseInfo.getCourseName() : "未知课程");
            
            // 获取教师信息
            Teacher teacher = teacherService.getOne(
                    new QueryWrapper<Teacher>().eq("teacher_no", teacherNo));
            suggestion.setTeacherName(teacher != null ? teacher.getRealname() : "未知教师");
            
            // 设置班级名称
            suggestion.setClassName(classNo);
            
            // 获取已有课程计划
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> existingPlans = coursePlanService.list(wrapper);
            
            // 生成时间段建议
            suggestion.setSuggestedTimeSlots(generateTimeSlotSuggestions(
                    teacherNo, classNo, existingPlans));
            
            // 生成教室建议
            suggestion.setSuggestedClassrooms(generateClassroomSuggestions(
                    courseNo, existingPlans));
            
            // 设置优先级
            suggestion.setPriority("MEDIUM");
            suggestion.setReason("基于教师空闲时间、班级课表和教室可用性的智能分析");
            
            return ServerResponse.ofSuccess(suggestion);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取排课建议失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量获取排课建议
     */
    @GetMapping("/batch")
    public ServerResponse getBatchSuggestions(@RequestParam(required = false) String semester) {
        try {
            // 获取所有未安排的课程任务
            QueryWrapper<ClassTask> taskWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                taskWrapper.eq("semester", semester);
            }
            List<ClassTask> tasks = classTaskService.list(taskWrapper);
            
            // 获取已有课程计划
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> existingPlans = coursePlanService.list(planWrapper);
            Set<String> scheduledTasks = existingPlans.stream()
                    .map(p -> p.getCourseNo() + "_" + p.getClassNo())
                    .collect(Collectors.toSet());
            
            // 找出未安排的任务
            List<ScheduleSuggestionVO> suggestions = new ArrayList<>();
            for (ClassTask task : tasks) {
                String key = task.getCourseNo() + "_" + task.getClassNo();
                if (!scheduledTasks.contains(key) && task.getTeacherNo() != null) {
                    ScheduleSuggestionVO suggestion = new ScheduleSuggestionVO();
                    suggestion.setSuggestionId(UUID.randomUUID().toString());
                    suggestion.setCourseNo(task.getCourseNo());
                    suggestion.setTeacherNo(task.getTeacherNo());
                    suggestion.setClassNo(task.getClassNo());
                    
                    // 获取课程和教师信息
                    CourseInfo courseInfo = courseInfoService.getOne(
                            new QueryWrapper<CourseInfo>().eq("course_no", task.getCourseNo()));
                    suggestion.setCourseName(courseInfo != null ? courseInfo.getCourseName() : "未知课程");
                    
                    Teacher teacher = teacherService.getOne(
                            new QueryWrapper<Teacher>().eq("teacher_no", task.getTeacherNo()));
                    suggestion.setTeacherName(teacher != null ? teacher.getRealname() : "未知教师");
                    
                    suggestion.setClassName(task.getClassNo());
                    suggestion.setPriority("HIGH");
                    suggestion.setReason("未安排的课程任务");
                    
                    suggestions.add(suggestion);
                }
            }
            
            return ServerResponse.ofSuccess(suggestions);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取批量建议失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成时间段建议
     */
    private List<ScheduleSuggestionVO.TimeSlotSuggestion> generateTimeSlotSuggestions(
            String teacherNo, String classNo, List<CoursePlan> existingPlans) {
        
        List<ScheduleSuggestionVO.TimeSlotSuggestion> suggestions = new ArrayList<>();
        
        // 定义时间段
        String[] timeSlots = {
            "周一1-2节", "周一3-4节", "周一5-6节", "周一7-8节",
            "周二1-2节", "周二3-4节", "周二5-6节", "周二7-8节",
            "周三1-2节", "周三3-4节", "周三5-6节", "周三7-8节",
            "周四1-2节", "周四3-4节", "周四5-6节", "周四7-8节",
            "周五1-2节", "周五3-4节", "周五5-6节", "周五7-8节"
        };
        
        // 统计已占用的时间段
        Map<String, Integer> teacherBusySlots = new HashMap<>();
        Map<String, Integer> classBusySlots = new HashMap<>();
        
        for (CoursePlan plan : existingPlans) {
            if (plan.getTeacherNo().equals(teacherNo)) {
                teacherBusySlots.merge(plan.getClassTime(), 1, Integer::sum);
            }
            if (plan.getClassNo().equals(classNo)) {
                classBusySlots.merge(plan.getClassTime(), 1, Integer::sum);
            }
        }
        
        // 评估每个时间段
        for (int i = 0; i < timeSlots.length; i++) {
            String slot = timeSlots[i];
            ScheduleSuggestionVO.TimeSlotSuggestion suggestion = 
                    new ScheduleSuggestionVO.TimeSlotSuggestion();
            
            suggestion.setTimeSlot(slot);
            suggestion.setDayOfWeek((i / 4) + 1);
            suggestion.setPeriod(getPeriod(i % 4));
            
            // 计算评分
            int score = 100;
            StringBuilder reason = new StringBuilder();
            
            // 教师是否空闲
            if (teacherBusySlots.containsKey(slot)) {
                score = 0;
                reason.append("教师已有课程；");
            }
            
            // 班级是否空闲
            if (classBusySlots.containsKey(slot)) {
                score = 0;
                reason.append("班级已有课程；");
            }
            
            // 时间段偏好（上午优于下午）
            if (score > 0) {
                int period = i % 4;
                if (period == 0 || period == 1) {
                    score += 10;
                    reason.append("上午时段，学习效果好；");
                } else if (period == 3) {
                    score -= 10;
                    reason.append("最后一节，注意力可能下降；");
                }
                
                // 避免连续多节课
                int dayOfWeek = i / 4;
                int sameDay = 0;
                for (int j = dayOfWeek * 4; j < (dayOfWeek + 1) * 4; j++) {
                    if (teacherBusySlots.containsKey(timeSlots[j])) {
                        sameDay++;
                    }
                }
                if (sameDay >= 3) {
                    score -= 15;
                    reason.append("当天课程较多；");
                }
            }
            
            suggestion.setScore(Math.max(0, Math.min(100, score)));
            suggestion.setScoreReason(reason.length() > 0 ? reason.toString() : "推荐时段");
            suggestion.setRecommended(score >= 80);
            
            suggestions.add(suggestion);
        }
        
        // 按评分排序
        suggestions.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        
        // 只返回前10个
        return suggestions.stream().limit(10).collect(Collectors.toList());
    }
    
    /**
     * 生成教室建议
     */
    private List<ScheduleSuggestionVO.ClassroomSuggestion> generateClassroomSuggestions(
            String courseNo, List<CoursePlan> existingPlans) {
        
        List<ScheduleSuggestionVO.ClassroomSuggestion> suggestions = new ArrayList<>();
        
        // 获取所有教室
        List<Classroom> classrooms = classroomService.list();
        
        // 统计教室使用情况
        Map<String, Integer> classroomUsage = new HashMap<>();
        for (CoursePlan plan : existingPlans) {
            if (plan.getClassroomNo() != null) {
                classroomUsage.merge(plan.getClassroomNo(), 1, Integer::sum);
            }
        }
        
        // 评估每个教室
        for (Classroom classroom : classrooms) {
            ScheduleSuggestionVO.ClassroomSuggestion suggestion = 
                    new ScheduleSuggestionVO.ClassroomSuggestion();
            
            suggestion.setClassroomNo(classroom.getClassroomNo());
            suggestion.setClassroomName(classroom.getClassroomName());
            suggestion.setBuildingName(classroom.getTeachbuildNo());
            suggestion.setCapacity(classroom.getCapacity() != null ? classroom.getCapacity() : 50);
            
            // 计算评分
            int score = 100;
            StringBuilder reason = new StringBuilder();
            
            // 使用频率（使用少的优先）
            int usage = classroomUsage.getOrDefault(classroom.getClassroomNo(), 0);
            if (usage == 0) {
                score += 10;
                reason.append("空闲教室；");
            } else if (usage > 15) {
                score -= 20;
                reason.append("使用频繁；");
            }
            
            // 容量适配
            int capacity = classroom.getCapacity() != null ? classroom.getCapacity() : 50;
            if (capacity >= 40 && capacity <= 60) {
                score += 15;
                reason.append("容量适中；");
            } else if (capacity < 30) {
                score -= 10;
                reason.append("容量较小；");
            }
            
            // 教室属性
            if (classroom.getAttr() != null && !classroom.getAttr().isEmpty()) {
                score += 5;
                reason.append("设施完善；");
            }
            
            suggestion.setScore(Math.max(0, Math.min(100, score)));
            suggestion.setScoreReason(reason.length() > 0 ? reason.toString() : "推荐教室");
            suggestion.setRecommended(score >= 85);
            
            suggestions.add(suggestion);
        }
        
        // 按评分排序
        suggestions.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        
        // 只返回前10个
        return suggestions.stream().limit(10).collect(Collectors.toList());
    }
    
    /**
     * 获取节次描述
     */
    private String getPeriod(int index) {
        switch (index) {
            case 0: return "1-2节";
            case 1: return "3-4节";
            case 2: return "5-6节";
            case 3: return "7-8节";
            default: return "未知";
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    public ServerResponse getStats(@RequestParam(required = false) String semester) {
        try {
            QueryWrapper<ClassTask> taskWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                taskWrapper.eq("semester", semester);
            }
            List<ClassTask> tasks = classTaskService.list(taskWrapper);
            
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> plans = coursePlanService.list(planWrapper);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTasks", tasks.size());
            stats.put("scheduledTasks", plans.size());
            stats.put("unscheduledTasks", Math.max(0, tasks.size() - plans.size()));
            stats.put("scheduleRate", tasks.size() > 0 ? 
                    Math.round((double) plans.size() / tasks.size() * 100) : 0);
            
            return ServerResponse.ofSuccess(stats);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
}
