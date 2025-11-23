package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.entity.response.TeacherWorkloadVO;
import com.chq.coursearrange.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 教师工作量统计控制器
 * @author CHQ
 */
@RestController
@RequestMapping("/teacher/workload")
public class TeacherWorkloadController {

    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private CourseInfoService courseInfoService;
    
    @Autowired
    private ClassInfoService classInfoService;
    
    @Autowired
    private ClassTaskService classTaskService;

    /**
     * 获取所有教师工作量统计
     * @param semester 学期（可选）
     * @return 教师工作量列表
     */
    @GetMapping("/all")
    public ServerResponse getAllTeacherWorkload(@RequestParam(required = false) String semester) {
        try {
            // 获取所有教师
            List<Teacher> teachers = teacherService.list();
            
            // 获取课程计划
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(planWrapper);
            
            // 按教师分组统计
            Map<String, List<CoursePlan>> teacherPlansMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String teacherNo = plan.getTeacherNo();
                teacherPlansMap.computeIfAbsent(teacherNo, k -> new ArrayList<>()).add(plan);
            }
            
            // 构建响应数据
            List<TeacherWorkloadVO> result = new ArrayList<>();
            
            for (Teacher teacher : teachers) {
                TeacherWorkloadVO vo = new TeacherWorkloadVO();
                vo.setId(teacher.getId());
                vo.setTeacherNo(teacher.getTeacherNo());
                vo.setTeacherName(teacher.getRealname());
                vo.setDepartment(teacher.getTeach() != null ? teacher.getTeach() : "未分配");
                vo.setTitle(teacher.getJobtitle() != null ? teacher.getJobtitle() : "讲师");
                
                // 获取该教师的课程计划
                List<CoursePlan> teacherPlans = teacherPlansMap.getOrDefault(
                        teacher.getTeacherNo(), new ArrayList<>());
                
                // 计算工作量
                int totalHours = 0;
                int weeklyHours = 0;
                Set<String> classSet = new HashSet<>();
                int studentCount = 0;
                List<TeacherWorkloadVO.CourseDetail> courseDetails = new ArrayList<>();
                
                for (CoursePlan plan : teacherPlans) {
                    // 获取课程信息
                    CourseInfo courseInfo = courseInfoService.getOne(
                            new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
                    
                    // 获取班级信息
                    ClassInfo classInfo = classInfoService.getOne(
                            new QueryWrapper<ClassInfo>().eq("class_no", plan.getClassNo()));
                    
                    // 获取课程任务信息（包含周学时）
                    QueryWrapper<ClassTask> taskWrapper = new QueryWrapper<>();
                    taskWrapper.eq("course_no", plan.getCourseNo())
                              .eq("class_no", plan.getClassNo());
                    if (semester != null && !semester.isEmpty()) {
                        taskWrapper.eq("semester", semester);
                    }
                    ClassTask classTask = classTaskService.getOne(taskWrapper);
                    
                    int hours = classTask != null ? classTask.getWeeksNumber() : 2;
                    int weeks = plan.getWeeksSum() != null ? plan.getWeeksSum() : 16;
                    
                    totalHours += hours * weeks;
                    weeklyHours += hours;
                    classSet.add(plan.getClassNo());
                    
                    if (classTask != null && classTask.getStudentNum() != null) {
                        studentCount += classTask.getStudentNum();
                    }
                    
                    // 构建课程详情
                    TeacherWorkloadVO.CourseDetail detail = new TeacherWorkloadVO.CourseDetail();
                    detail.setCourseNo(plan.getCourseNo());
                    detail.setCourseName(courseInfo != null ? courseInfo.getCourseName() : "未知课程");
                    detail.setClassName(classInfo != null ? classInfo.getClassName() : "未知班级");
                    detail.setWeeklyHours(hours);
                    detail.setStudentCount(classTask != null ? classTask.getStudentNum() : 0);
                    detail.setClassTime(plan.getClassTime());
                    courseDetails.add(detail);
                }
                
                vo.setTotalHours(totalHours);
                vo.setWeeklyHours(weeklyHours);
                vo.setClassCount(classSet.size());
                vo.setStudentCount(studentCount);
                vo.setCourses(courseDetails);
                
                // 计算工作负荷等级（标准周课时12-16）
                double percentage = (weeklyHours / 14.0) * 100;
                vo.setWorkloadPercentage(Math.round(percentage * 100.0) / 100.0);
                
                if (weeklyHours < 8) {
                    vo.setWorkloadLevel("low");
                } else if (weeklyHours <= 16) {
                    vo.setWorkloadLevel("normal");
                } else if (weeklyHours <= 20) {
                    vo.setWorkloadLevel("high");
                } else {
                    vo.setWorkloadLevel("overload");
                }
                
                result.add(vo);
            }
            
            // 按周课时降序排序
            result.sort((a, b) -> b.getWeeklyHours().compareTo(a.getWeeklyHours()));
            
            return ServerResponse.ofSuccess(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取教师工作量数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取工作量统计摘要
     * @param semester 学期
     * @return 统计摘要
     */
    @GetMapping("/summary")
    public ServerResponse getWorkloadSummary(@RequestParam(required = false) String semester) {
        try {
            List<Teacher> teachers = teacherService.list();
            
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(planWrapper);
            
            // 统计数据
            int totalTeachers = teachers.size();
            int activeTeachers = 0;
            int totalHours = 0;
            int lowWorkload = 0;
            int normalWorkload = 0;
            int highWorkload = 0;
            int overload = 0;
            
            Map<String, Integer> teacherHoursMap = new HashMap<>();
            
            for (CoursePlan plan : coursePlans) {
                QueryWrapper<ClassTask> taskWrapper = new QueryWrapper<>();
                taskWrapper.eq("course_no", plan.getCourseNo())
                          .eq("class_no", plan.getClassNo());
                if (semester != null && !semester.isEmpty()) {
                    taskWrapper.eq("semester", semester);
                }
                ClassTask classTask = classTaskService.getOne(taskWrapper);
                
                int hours = classTask != null ? classTask.getWeeksNumber() : 2;
                teacherHoursMap.merge(plan.getTeacherNo(), hours, Integer::sum);
            }
            
            for (Integer hours : teacherHoursMap.values()) {
                activeTeachers++;
                totalHours += hours;
                
                if (hours < 8) {
                    lowWorkload++;
                } else if (hours <= 16) {
                    normalWorkload++;
                } else if (hours <= 20) {
                    highWorkload++;
                } else {
                    overload++;
                }
            }
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalTeachers", totalTeachers);
            summary.put("activeTeachers", activeTeachers);
            summary.put("averageHours", activeTeachers > 0 ? 
                    Math.round((double) totalHours / activeTeachers * 100.0) / 100.0 : 0);
            summary.put("totalHours", totalHours);
            summary.put("lowWorkload", lowWorkload);
            summary.put("normalWorkload", normalWorkload);
            summary.put("highWorkload", highWorkload);
            summary.put("overload", overload);
            
            return ServerResponse.ofSuccess(summary);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取统计摘要失败：" + e.getMessage());
        }
    }
}
