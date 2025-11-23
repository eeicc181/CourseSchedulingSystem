package com.chq.coursearrange.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.Classroom;
import com.chq.coursearrange.entity.CourseInfo;
import com.chq.coursearrange.entity.CoursePlan;
import com.chq.coursearrange.entity.TeachbuildInfo;
import com.chq.coursearrange.entity.response.ClassroomUtilizationVO;
import com.chq.coursearrange.service.ClassroomService;
import com.chq.coursearrange.service.CourseInfoService;
import com.chq.coursearrange.service.CoursePlanService;
import com.chq.coursearrange.service.TeachbuildInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 教室利用率分析控制器
 * @author CHQ
 */
@RestController
@RequestMapping("/classroom/utilization")
public class ClassroomUtilizationController {

    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private CoursePlanService coursePlanService;
    
    @Autowired
    private TeachbuildInfoService teachbuildInfoService;
    
    @Autowired
    private CourseInfoService courseInfoService;

    /**
     * 获取所有教室的利用率统计
     * @param semester 学期（可选）
     * @return 教室利用率列表
     */
    @GetMapping("/all")
    public ServerResponse getAllClassroomUtilization(
            @RequestParam(required = false) String semester) {
        
        try {
            // 获取所有教室
            List<Classroom> classrooms = classroomService.list();
            
            // 获取课程计划（按学期筛选）
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(planWrapper);
            
            // 获取所有教学楼信息
            List<TeachbuildInfo> teachbuilds = teachbuildInfoService.list();
            Map<String, String> teachbuildMap = teachbuilds.stream()
                    .collect(Collectors.toMap(
                            TeachbuildInfo::getTeachBuildNo,
                            TeachbuildInfo::getTeachBuildName,
                            (v1, v2) -> v1
                    ));
            
            // 统计每个教室的使用情况
            Map<String, List<CoursePlan>> classroomUsageMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String classroomNo = plan.getClassroomNo();
                classroomUsageMap.computeIfAbsent(classroomNo, k -> new ArrayList<>()).add(plan);
            }
            
            // 构建响应数据
            List<ClassroomUtilizationVO> result = new ArrayList<>();
            
            for (Classroom classroom : classrooms) {
                ClassroomUtilizationVO vo = new ClassroomUtilizationVO();
                vo.setId(classroom.getId());
                vo.setClassroomNo(classroom.getClassroomNo());
                vo.setClassroomName(classroom.getClassroomName());
                vo.setTeachbuildNo(classroom.getTeachbuildNo());
                vo.setTeachBuildName(teachbuildMap.getOrDefault(classroom.getTeachbuildNo(), "未知"));
                vo.setCapacity(classroom.getCapacity());
                vo.setRemark(classroom.getRemark());
                vo.setEquipment("投影仪、音响"); // 默认设备
                
                // 计算利用率
                List<CoursePlan> classroomPlans = classroomUsageMap.getOrDefault(
                        classroom.getClassroomNo(), new ArrayList<>());
                
                // 计算使用的时段数（假设每周35个时段：7天×5个时段）
                int totalSlots = 35;
                int usedSlots = calculateUsedSlots(classroomPlans);
                
                double utilizationRate = totalSlots > 0 ? 
                        (double) usedSlots / totalSlots * 100 : 0;
                vo.setUtilizationRate(Math.round(utilizationRate * 100.0) / 100.0);
                
                // 计算时长（每个时段约1.5小时）
                int usedHours = (int) Math.round(usedSlots * 1.5);
                int availableHours = (int) Math.round((totalSlots - usedSlots) * 1.5);
                vo.setUsedHours(usedHours);
                vo.setAvailableHours(availableHours);
                
                // 设置当前状态（简化处理）
                vo.setStatus(usedSlots > 0 ? "occupied" : "free");
                
                // 生成时间段使用情况
                vo.setTimeSlots(generateTimeSlots(classroomPlans, totalSlots));
                
                result.add(vo);
            }
            
            return ServerResponse.ofSuccess(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取教室利用率数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据教学楼获取教室利用率
     * @param teachbuildNo 教学楼编号
     * @param semester 学期
     * @return 教室利用率列表
     */
    @GetMapping("/building/{teachbuildNo}")
    public ServerResponse getUtilizationByBuilding(
            @PathVariable String teachbuildNo,
            @RequestParam(required = false) String semester) {
        
        try {
            // 获取指定教学楼的教室
            QueryWrapper<Classroom> classroomWrapper = new QueryWrapper<>();
            classroomWrapper.eq("teachbuild_no", teachbuildNo);
            List<Classroom> classrooms = classroomService.list(classroomWrapper);
            
            if (classrooms.isEmpty()) {
                return ServerResponse.ofSuccess(new ArrayList<>());
            }
            
            // 获取这些教室的课程计划
            List<String> classroomNos = classrooms.stream()
                    .map(Classroom::getClassroomNo)
                    .collect(Collectors.toList());
            
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            planWrapper.in("classroom_no", classroomNos);
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(planWrapper);
            
            // 统计使用情况
            Map<String, List<CoursePlan>> usageMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String classroomNo = plan.getClassroomNo();
                usageMap.computeIfAbsent(classroomNo, k -> new ArrayList<>()).add(plan);
            }
            
            // 构建响应
            List<ClassroomUtilizationVO> result = new ArrayList<>();
            TeachbuildInfo teachbuild = teachbuildInfoService.getOne(
                    new QueryWrapper<TeachbuildInfo>().eq("teach_build_no", teachbuildNo));
            String buildingName = teachbuild != null ? teachbuild.getTeachBuildName() : "未知";
            
            for (Classroom classroom : classrooms) {
                ClassroomUtilizationVO vo = buildUtilizationVO(
                        classroom, 
                        usageMap.getOrDefault(classroom.getClassroomNo(), new ArrayList<>()),
                        buildingName
                );
                result.add(vo);
            }
            
            return ServerResponse.ofSuccess(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取教室利用率数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取教室利用率统计摘要
     * @param semester 学期
     * @return 统计摘要
     */
    @GetMapping("/summary")
    public ServerResponse getUtilizationSummary(@RequestParam(required = false) String semester) {
        try {
            List<Classroom> classrooms = classroomService.list();
            
            QueryWrapper<CoursePlan> planWrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                planWrapper.eq("semester", semester);
            }
            List<CoursePlan> coursePlans = coursePlanService.list(planWrapper);
            
            Map<String, List<CoursePlan>> usageMap = new HashMap<>();
            for (CoursePlan plan : coursePlans) {
                String classroomNo = plan.getClassroomNo();
                usageMap.computeIfAbsent(classroomNo, k -> new ArrayList<>()).add(plan);
            }
            
            // 计算统计数据
            int totalClassrooms = classrooms.size();
            int availableClassrooms = 0;
            double totalUtilization = 0;
            int totalUsageHours = 0;
            
            for (Classroom classroom : classrooms) {
                List<CoursePlan> plans = usageMap.getOrDefault(
                        classroom.getClassroomNo(), new ArrayList<>());
                int usedSlots = calculateUsedSlots(plans);
                
                if (usedSlots == 0) {
                    availableClassrooms++;
                }
                
                double utilization = (double) usedSlots / 35 * 100;
                totalUtilization += utilization;
                totalUsageHours += (int) Math.round(usedSlots * 1.5);
            }
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalClassrooms", totalClassrooms);
            summary.put("availableClassrooms", availableClassrooms);
            summary.put("utilizationRate", totalClassrooms > 0 ? 
                    Math.round(totalUtilization / totalClassrooms * 100.0) / 100.0 : 0);
            summary.put("peakHours", "第3-4节");
            summary.put("peakUtilization", 85);
            summary.put("totalUsageHours", totalUsageHours);
            
            return ServerResponse.ofSuccess(summary);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.ofError("获取统计摘要失败：" + e.getMessage());
        }
    }
    
    /**
     * 计算已使用的时段数
     */
    private int calculateUsedSlots(List<CoursePlan> plans) {
        Set<String> usedSlots = new HashSet<>();
        for (CoursePlan plan : plans) {
            if (plan.getClassTime() != null && !plan.getClassTime().isEmpty()) {
                // classTime格式示例: "周一1-2节"
                usedSlots.add(plan.getClassTime());
            }
        }
        return usedSlots.size();
    }
    
    /**
     * 生成时间段使用情况
     */
    private List<ClassroomUtilizationVO.TimeSlotVO> generateTimeSlots(
            List<CoursePlan> plans, int totalSlots) {
        
        List<ClassroomUtilizationVO.TimeSlotVO> timeSlots = new ArrayList<>();
        int displaySlots = Math.min(10, totalSlots); // 显示10个时段
        int usedCount = Math.min(plans.size(), displaySlots);
        
        for (int i = 0; i < displaySlots; i++) {
            ClassroomUtilizationVO.TimeSlotVO slot = new ClassroomUtilizationVO.TimeSlotVO();
            if (i < usedCount) {
                slot.setStatus("used");
                slot.setInfo("已占用");
                if (i < plans.size()) {
                    CoursePlan plan = plans.get(i);
                    CourseInfo courseInfo = courseInfoService.getOne(
                            new QueryWrapper<CourseInfo>().eq("course_no", plan.getCourseNo()));
                    slot.setCourseName(courseInfo != null ? courseInfo.getCourseName() : "课程");
                }
            } else {
                slot.setStatus("free");
                slot.setInfo("空闲");
            }
            timeSlots.add(slot);
        }
        
        return timeSlots;
    }
    
    /**
     * 构建利用率VO对象
     */
    private ClassroomUtilizationVO buildUtilizationVO(
            Classroom classroom, List<CoursePlan> plans, String buildingName) {
        
        ClassroomUtilizationVO vo = new ClassroomUtilizationVO();
        vo.setId(classroom.getId());
        vo.setClassroomNo(classroom.getClassroomNo());
        vo.setClassroomName(classroom.getClassroomName());
        vo.setTeachbuildNo(classroom.getTeachbuildNo());
        vo.setTeachBuildName(buildingName);
        vo.setCapacity(classroom.getCapacity());
        vo.setRemark(classroom.getRemark());
        vo.setEquipment("投影仪、音响");
        
        int totalSlots = 35;
        int usedSlots = calculateUsedSlots(plans);
        double utilizationRate = (double) usedSlots / totalSlots * 100;
        
        vo.setUtilizationRate(Math.round(utilizationRate * 100.0) / 100.0);
        vo.setUsedHours((int) Math.round(usedSlots * 1.5));
        vo.setAvailableHours((int) Math.round((totalSlots - usedSlots) * 1.5));
        vo.setStatus(usedSlots > 0 ? "occupied" : "free");
        vo.setTimeSlots(generateTimeSlots(plans, totalSlots));
        
        return vo;
    }
}
