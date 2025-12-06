package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 移动端课表控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/mobile/schedule")
@Api(tags = "移动端课表查询")
@CrossOrigin(origins = "*")  // 允许跨域访问
public class MobileScheduleController {
    
    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private CourseInfoService courseInfoService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private ClassroomService classroomService;
    
    /**
     * 获取学生课表（按周显示）
     */
    @GetMapping("/student/{studentNo}")
    @ApiOperation("获取学生课表")
    public ServerResponse getStudentSchedule(
            @ApiParam("学号") @PathVariable String studentNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            // 如果没有指定学期，使用当前学期
            if (semester == null || semester.isEmpty()) {
                semester = getCurrentSemester();
            }
            
            // 获取学生班级信息（这里简化处理，实际应该从学生表获取）
            // 假设学号前8位是班级编号
            String classNo = studentNo.substring(0, 8);
            
            // 查询课表
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            wrapper.eq("semester", semester)
                   .eq("class_no", classNo)
                   .orderBy(true, true, "class_time");
            
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 组织成周视图数据
            Map<String, Object> result = organizeWeekView(coursePlans);
            result.put("semester", semester);
            result.put("studentNo", studentNo);
            result.put("classNo", classNo);
            result.put("currentWeek", getCurrentWeek());
            
            return ServerResponse.ofSuccess(result);
        } catch (Exception e) {
            return ServerResponse.ofError("获取学生课表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取教师课表（按周显示）
     */
    @GetMapping("/teacher/{teacherNo}")
    @ApiOperation("获取教师课表")
    public ServerResponse getTeacherSchedule(
            @ApiParam("教师编号") @PathVariable String teacherNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            if (semester == null || semester.isEmpty()) {
                semester = getCurrentSemester();
            }
            
            // 查询课表
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            wrapper.eq("semester", semester)
                   .eq("teacher_no", teacherNo)
                   .orderBy(true, true, "class_time");
            
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 组织成周视图数据
            Map<String, Object> result = organizeWeekView(coursePlans);
            result.put("semester", semester);
            result.put("teacherNo", teacherNo);
            result.put("currentWeek", getCurrentWeek());
            
            return ServerResponse.ofSuccess(result);
        } catch (Exception e) {
            return ServerResponse.ofError("获取教师课表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取今日课程
     */
    @GetMapping("/today/{userNo}")
    @ApiOperation("获取今日课程")
    public ServerResponse getTodaySchedule(
            @ApiParam("用户编号") @PathVariable String userNo,
            @ApiParam("用户类型：student/teacher") @RequestParam String userType,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            if (semester == null || semester.isEmpty()) {
                semester = getCurrentSemester();
            }
            
            // 获取今天是星期几
            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
            String todayPrefix = getDayPrefix(dayOfWeek);
            
            // 查询课表
            QueryWrapper<CoursePlan> wrapper = new QueryWrapper<>();
            wrapper.eq("semester", semester);
            
            if ("student".equals(userType)) {
                String classNo = userNo.substring(0, 8);
                wrapper.eq("class_no", classNo);
            } else {
                wrapper.eq("teacher_no", userNo);
            }
            
            wrapper.like("class_time", todayPrefix)
                   .orderBy(true, true, "class_time");
            
            List<CoursePlan> coursePlans = coursePlanService.list(wrapper);
            
            // 填充详细信息
            List<Map<String, Object>> todayCourses = new ArrayList<>();
            for (CoursePlan plan : coursePlans) {
                Map<String, Object> course = new HashMap<>();
                course.put("id", plan.getId());
                course.put("classTime", plan.getClassTime());
                course.put("timeSlot", parseTimeSlot(plan.getClassTime()));
                
                // 课程信息
                CourseInfo courseInfo = courseInfoService.getOne(
                    new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
                course.put("courseName", courseInfo != null ? courseInfo.getCourseName() : "未知课程");
                course.put("courseNo", plan.getCourseNo());
                
                // 教师信息
                Teacher teacher = teacherService.getOne(
                    new QueryWrapper<Teacher>().eq("teacher_no", plan.getTeacherNo()));
                course.put("teacherName", teacher != null ? teacher.getRealname() : "未分配");
                
                // 教室信息
                Classroom classroom = classroomService.getOne(
                    new QueryWrapper<Classroom>().eq("classroom_no", plan.getClassroomNo()));
                course.put("classroomName", classroom != null ? classroom.getClassroomName() : "未分配");
                course.put("classroomNo", plan.getClassroomNo());
                
                // 班级信息
                course.put("classNo", plan.getClassNo());
                
                todayCourses.add(course);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            result.put("dayOfWeek", getDayName(dayOfWeek));
            result.put("courses", todayCourses);
            result.put("totalCount", todayCourses.size());
            
            return ServerResponse.ofSuccess(result);
        } catch (Exception e) {
            return ServerResponse.ofError("获取今日课程失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取课程详情
     */
    @GetMapping("/detail/{planId}")
    @ApiOperation("获取课程详情")
    public ServerResponse getCourseDetail(@ApiParam("课程计划ID") @PathVariable Integer planId) {
        try {
            CoursePlan plan = coursePlanService.getById(planId);
            if (plan == null) {
                return ServerResponse.ofError("课程不存在");
            }
            
            Map<String, Object> detail = new HashMap<>();
            
            // 基本信息
            detail.put("id", plan.getId());
            detail.put("classTime", plan.getClassTime());
            detail.put("timeSlot", parseTimeSlot(plan.getClassTime()));
            detail.put("semester", plan.getSemester());
            
            // 课程信息
            CourseInfo courseInfo = courseInfoService.getOne(
                new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
            if (courseInfo != null) {
                detail.put("courseName", courseInfo.getCourseName());
                detail.put("courseNo", courseInfo.getCourseNo());
                detail.put("courseAttr", courseInfo.getCourseAttr());
            }
            
            // 教师信息
            Teacher teacher = teacherService.getOne(
                new QueryWrapper<Teacher>().eq("teacher_no", plan.getTeacherNo()));
            if (teacher != null) {
                detail.put("teacherName", teacher.getRealname());
                detail.put("teacherNo", teacher.getTeacherNo());
            }
            
            // 教室信息
            Classroom classroom = classroomService.getOne(
                new QueryWrapper<Classroom>().eq("classroom_no", plan.getClassroomNo()));
            if (classroom != null) {
                detail.put("classroomName", classroom.getClassroomName());
                detail.put("classroomNo", classroom.getClassroomNo());
                detail.put("capacity", classroom.getCapacity());
                detail.put("building", classroom.getTeachbuildNo());
            }
            
            // 班级信息
            detail.put("classNo", plan.getClassNo());
            detail.put("gradeNo", plan.getGradeNo());
            
            return ServerResponse.ofSuccess(detail);
        } catch (Exception e) {
            return ServerResponse.ofError("获取课程详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 组织成周视图数据
     */
    private Map<String, Object> organizeWeekView(List<CoursePlan> coursePlans) {
        Map<String, Object> result = new HashMap<>();
        
        // 按星期分组
        Map<String, List<Map<String, Object>>> weekData = new LinkedHashMap<>();
        weekData.put("周一", new ArrayList<>());
        weekData.put("周二", new ArrayList<>());
        weekData.put("周三", new ArrayList<>());
        weekData.put("周四", new ArrayList<>());
        weekData.put("周五", new ArrayList<>());
        weekData.put("周六", new ArrayList<>());
        weekData.put("周日", new ArrayList<>());
        
        for (CoursePlan plan : coursePlans) {
            String classTime = plan.getClassTime();
            String dayName = parseDayName(classTime);
            
            Map<String, Object> course = new HashMap<>();
            course.put("id", plan.getId());
            course.put("classTime", classTime);
            course.put("timeSlot", parseTimeSlot(classTime));
            
            // 获取课程名称
            CourseInfo courseInfo = courseInfoService.getOne(
                new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
            course.put("courseName", courseInfo != null ? courseInfo.getCourseName() : "未知课程");
            
            // 获取教师姓名
            Teacher teacher = teacherService.getOne(
                new QueryWrapper<Teacher>().eq("teacher_no", plan.getTeacherNo()));
            course.put("teacherName", teacher != null ? teacher.getRealname() : "未分配");
            
            // 获取教室
            Classroom classroom = classroomService.getOne(
                new QueryWrapper<Classroom>().eq("classroom_no", plan.getClassroomNo()));
            course.put("classroomName", classroom != null ? classroom.getClassroomName() : "未分配");
            
            weekData.get(dayName).add(course);
        }
        
        result.put("weekData", weekData);
        result.put("totalCourses", coursePlans.size());
        
        return result;
    }
    
    /**
     * 解析星期名称
     */
    private String parseDayName(String classTime) {
        if (classTime.startsWith("周一")) return "周一";
        if (classTime.startsWith("周二")) return "周二";
        if (classTime.startsWith("周三")) return "周三";
        if (classTime.startsWith("周四")) return "周四";
        if (classTime.startsWith("周五")) return "周五";
        if (classTime.startsWith("周六")) return "周六";
        if (classTime.startsWith("周日")) return "周日";
        return "未知";
    }
    
    /**
     * 解析时间段
     */
    private String parseTimeSlot(String classTime) {
        if (classTime.contains("1-2节")) return "08:00-09:40";
        if (classTime.contains("3-4节")) return "10:00-11:40";
        if (classTime.contains("5-6节")) return "14:00-15:40";
        if (classTime.contains("7-8节")) return "16:00-17:40";
        return "未知";
    }
    
    /**
     * 获取星期前缀
     */
    private String getDayPrefix(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }
    
    /**
     * 获取星期名称
     */
    private String getDayName(DayOfWeek dayOfWeek) {
        return getDayPrefix(dayOfWeek);
    }
    
    /**
     * 获取当前学期
     */
    private String getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        // 简单判断：9月-次年1月为第一学期，2月-7月为第二学期
        if (month >= 9 || month <= 1) {
            return year + "-" + (year + 1) + "-1";
        } else {
            return (year - 1) + "-" + year + "-2";
        }
    }
    
    /**
     * 获取当前周次
     */
    private int getCurrentWeek() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        return now.get(weekFields.weekOfYear());
    }
}
