package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
@Slf4j
public class DataAnalysisServiceImpl implements DataAnalysisService {
    
    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private ClassTaskService classTaskService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private CourseInfoService courseInfoService;
    
    @Override
    @Cacheable(value = "systemOverview", key = "#semester")
    public Map<String, Object> getSystemOverview(String semester) {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 基础统计
            int totalTeachers = teacherService.count();
            int totalStudents = studentService.count();
            int totalClassrooms = classroomService.count();
            int totalCourses = courseInfoService.count();
            
            // 排课统计
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            int scheduledCourses = coursePlanService.count(planWrapper);
            
            // 任务统计
            QueryWrapper<ClassTask> taskWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                taskWrapper.eq("semester", semester);
            }
            int totalTasks = classTaskService.count(taskWrapper);
            
            // 完成率
            double completionRate = totalTasks > 0 ? 
                (double) scheduledCourses / totalTasks * 100 : 0;
            
            overview.put("totalTeachers", totalTeachers);
            overview.put("totalStudents", totalStudents);
            overview.put("totalClassrooms", totalClassrooms);
            overview.put("totalCourses", totalCourses);
            overview.put("scheduledCourses", scheduledCourses);
            overview.put("totalTasks", totalTasks);
            overview.put("completionRate", String.format("%.1f", completionRate));
            
            log.info("系统概览数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取系统概览失败", e);
        }
        
        return overview;
    }
    
    @Override
    @Cacheable(value = "scheduleStatistics", key = "#semester")
    public Map<String, Object> getScheduleStatistics(String semester) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 按星期统计
            Map<String, Integer> weekDistribution = new LinkedHashMap<>();
            weekDistribution.put("周一", 0);
            weekDistribution.put("周二", 0);
            weekDistribution.put("周三", 0);
            weekDistribution.put("周四", 0);
            weekDistribution.put("周五", 0);
            weekDistribution.put("周六", 0);
            weekDistribution.put("周日", 0);
            
            // 按时间段统计
            Map<String, Integer> timeDistribution = new LinkedHashMap<>();
            timeDistribution.put("1-2节", 0);
            timeDistribution.put("3-4节", 0);
            timeDistribution.put("5-6节", 0);
            timeDistribution.put("7-8节", 0);
            
            for (CoursePlan plan : coursePlans) {
                String classTime = plan.getClassTime();
                
                // 统计星期
                for (String day : weekDistribution.keySet()) {
                    if (classTime.contains(day)) {
                        weekDistribution.put(day, weekDistribution.get(day) + 1);
                        break;
                    }
                }
                
                // 统计时间段
                for (String time : timeDistribution.keySet()) {
                    if (classTime.contains(time)) {
                        timeDistribution.put(time, timeDistribution.get(time) + 1);
                        break;
                    }
                }
            }
            
            statistics.put("totalCourses", coursePlans.size());
            statistics.put("weekDistribution", weekDistribution);
            statistics.put("timeDistribution", timeDistribution);
            
            log.info("排课统计数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取排课统计失败", e);
        }
        
        return statistics;
    }
    
    @Override
    @Cacheable(value = "classroomUtilization", key = "#semester")
    public Map<String, Object> getClassroomUtilization(String semester) {
        Map<String, Object> utilization = new HashMap<>();
        
        try {
            List<Classroom> classrooms = classroomService.list();
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 统计每个教室的使用次数
            Map<String, Integer> classroomUsage = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String classroomNo = plan.getClassroomNo();
                if (classroomNo != null && !classroomNo.isEmpty()) {
                    classroomUsage.put(classroomNo, 
                        classroomUsage.getOrDefault(classroomNo, 0) + 1);
                }
            }
            
            // 计算利用率（假设每周40个时间段）
            List<Map<String, Object>> classroomList = new ArrayList<>();
            for (Classroom classroom : classrooms) {
                Map<String, Object> item = new HashMap<>();
                item.put("classroomNo", classroom.getClassroomNo());
                item.put("classroomName", classroom.getClassroomName());
                item.put("capacity", classroom.getCapacity());
                
                int usage = classroomUsage.getOrDefault(classroom.getClassroomNo(), 0);
                double rate = (double) usage / 40 * 100;
                
                item.put("usageCount", usage);
                item.put("utilizationRate", String.format("%.1f", rate));
                
                classroomList.add(item);
            }
            
            // 按利用率排序
            classroomList.sort((a, b) -> {
                int usageA = (int) a.get("usageCount");
                int usageB = (int) b.get("usageCount");
                return Integer.compare(usageB, usageA);
            });
            
            // 统计利用率分布
            int highUtilization = 0;  // >75%
            int mediumUtilization = 0;  // 50-75%
            int lowUtilization = 0;  // <50%
            
            for (Map<String, Object> item : classroomList) {
                double rate = Double.parseDouble(
                    item.get("utilizationRate").toString().replace("%", ""));
                if (rate > 75) highUtilization++;
                else if (rate >= 50) mediumUtilization++;
                else lowUtilization++;
            }
            
            utilization.put("classrooms", classroomList);
            utilization.put("totalClassrooms", classrooms.size());
            utilization.put("highUtilization", highUtilization);
            utilization.put("mediumUtilization", mediumUtilization);
            utilization.put("lowUtilization", lowUtilization);
            
            log.info("教室利用率数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取教室利用率失败", e);
        }
        
        return utilization;
    }
    
    @Override
    @Cacheable(value = "teacherWorkload", key = "#semester")
    public Map<String, Object> getTeacherWorkload(String semester) {
        Map<String, Object> workload = new HashMap<>();
        
        try {
            List<Teacher> teachers = teacherService.list();
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 统计每个教师的课时
            Map<String, Integer> teacherHours = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String teacherNo = plan.getTeacherNo();
                if (teacherNo != null && !teacherNo.isEmpty()) {
                    teacherHours.put(teacherNo, 
                        teacherHours.getOrDefault(teacherNo, 0) + 2);  // 每节课2学时
                }
            }
            
            // 构建教师工作量列表
            List<Map<String, Object>> teacherList = new ArrayList<>();
            for (Teacher teacher : teachers) {
                Map<String, Object> item = new HashMap<>();
                item.put("teacherNo", teacher.getTeacherNo());
                item.put("teacherName", teacher.getRealname());
                
                int hours = teacherHours.getOrDefault(teacher.getTeacherNo(), 0);
                item.put("weeklyHours", hours);
                
                // 工作负荷等级
                String level;
                if (hours >= 16) level = "高";
                else if (hours >= 10) level = "中";
                else if (hours > 0) level = "低";
                else level = "无";
                
                item.put("workloadLevel", level);
                
                teacherList.add(item);
            }
            
            // 按课时排序
            teacherList.sort((a, b) -> {
                int hoursA = (int) a.get("weeklyHours");
                int hoursB = (int) b.get("weeklyHours");
                return Integer.compare(hoursB, hoursA);
            });
            
            // 统计工作负荷分布
            int highWorkload = 0;
            int mediumWorkload = 0;
            int lowWorkload = 0;
            int noWorkload = 0;
            
            for (Map<String, Object> item : teacherList) {
                String level = (String) item.get("workloadLevel");
                switch (level) {
                    case "高": highWorkload++; break;
                    case "中": mediumWorkload++; break;
                    case "低": lowWorkload++; break;
                    case "无": noWorkload++; break;
                }
            }
            
            workload.put("teachers", teacherList);
            workload.put("totalTeachers", teachers.size());
            workload.put("highWorkload", highWorkload);
            workload.put("mediumWorkload", mediumWorkload);
            workload.put("lowWorkload", lowWorkload);
            workload.put("noWorkload", noWorkload);
            
            log.info("教师工作量数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取教师工作量失败", e);
        }
        
        return workload;
    }
    
    @Override
    @Cacheable(value = "courseTimeDistribution", key = "#semester")
    public Map<String, Object> getCourseTimeDistribution(String semester) {
        Map<String, Object> distribution = new HashMap<>();
        
        try {
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 创建热力图数据（星期 x 时间段）
            String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            String[] times = {"1-2节", "3-4节", "5-6节", "7-8节"};
            
            int[][] heatmap = new int[7][4];
            
            for (CoursePlan plan : coursePlans) {
                String classTime = plan.getClassTime();
                
                for (int i = 0; i < days.length; i++) {
                    if (classTime.contains(days[i])) {
                        for (int j = 0; j < times.length; j++) {
                            if (classTime.contains(times[j])) {
                                heatmap[i][j]++;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            
            distribution.put("days", days);
            distribution.put("times", times);
            distribution.put("heatmap", heatmap);
            distribution.put("totalCourses", coursePlans.size());
            
            log.info("课程时间分布数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取课程时间分布失败", e);
        }
        
        return distribution;
    }
    
    @Override
    @Cacheable(value = "conflictStatistics", key = "#semester")
    public Map<String, Object> getConflictStatistics(String semester) {
        Map<String, Object> conflicts = new HashMap<>();
        
        try {
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            int teacherConflicts = 0;
            int classroomConflicts = 0;
            int classConflicts = 0;
            
            // 检测教师冲突
            Map<String, Set<String>> teacherTimeMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String key = plan.getTeacherNo() + "_" + plan.getClassTime();
                teacherTimeMap.computeIfAbsent(key, k -> new HashSet<>()).add(plan.getId().toString());
            }
            for (Set<String> ids : teacherTimeMap.values()) {
                if (ids.size() > 1) teacherConflicts++;
            }
            
            // 检测教室冲突
            Map<String, Set<String>> classroomTimeMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                if (plan.getClassroomNo() != null && !plan.getClassroomNo().isEmpty()) {
                    String key = plan.getClassroomNo() + "_" + plan.getClassTime();
                    classroomTimeMap.computeIfAbsent(key, k -> new HashSet<>()).add(plan.getId().toString());
                }
            }
            for (Set<String> ids : classroomTimeMap.values()) {
                if (ids.size() > 1) classroomConflicts++;
            }
            
            // 检测班级冲突
            Map<String, Set<String>> classTimeMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String key = plan.getClassNo() + "_" + plan.getClassTime();
                classTimeMap.computeIfAbsent(key, k -> new HashSet<>()).add(plan.getId().toString());
            }
            for (Set<String> ids : classTimeMap.values()) {
                if (ids.size() > 1) classConflicts++;
            }
            
            int totalConflicts = teacherConflicts + classroomConflicts + classConflicts;
            
            conflicts.put("totalConflicts", totalConflicts);
            conflicts.put("teacherConflicts", teacherConflicts);
            conflicts.put("classroomConflicts", classroomConflicts);
            conflicts.put("classConflicts", classConflicts);
            conflicts.put("conflictRate", coursePlans.size() > 0 ? 
                String.format("%.2f", (double) totalConflicts / coursePlans.size() * 100) : "0.00");
            
            log.info("冲突统计数据获取成功: {}", semester);
        } catch (Exception e) {
            log.error("获取冲突统计失败", e);
        }
        
        return conflicts;
    }
}
