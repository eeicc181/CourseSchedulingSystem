package com.chq.coursearrange.controller;

import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.service.DataAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据分析控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/analysis")
@Api(tags = "数据分析与可视化")
@CrossOrigin(origins = "*")
public class DataAnalysisController {
    
    @Autowired
    private DataAnalysisService dataAnalysisService;
    
    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    @ApiOperation("获取系统概览")
    public ServerResponse getSystemOverview(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> overview = dataAnalysisService.getSystemOverview(semester);
            return ServerResponse.ofSuccess(overview);
        } catch (Exception e) {
            return ServerResponse.ofError("获取系统概览失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取排课统计数据
     */
    @GetMapping("/schedule/statistics")
    @ApiOperation("获取排课统计")
    public ServerResponse getScheduleStatistics(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> statistics = dataAnalysisService.getScheduleStatistics(semester);
            return ServerResponse.ofSuccess(statistics);
        } catch (Exception e) {
            return ServerResponse.ofError("获取排课统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取教室利用率
     */
    @GetMapping("/classroom/utilization")
    @ApiOperation("获取教室利用率")
    public ServerResponse getClassroomUtilization(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> utilization = dataAnalysisService.getClassroomUtilization(semester);
            return ServerResponse.ofSuccess(utilization);
        } catch (Exception e) {
            return ServerResponse.ofError("获取教室利用率失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取教师工作量
     */
    @GetMapping("/teacher/workload")
    @ApiOperation("获取教师工作量")
    public ServerResponse getTeacherWorkload(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> workload = dataAnalysisService.getTeacherWorkload(semester);
            return ServerResponse.ofSuccess(workload);
        } catch (Exception e) {
            return ServerResponse.ofError("获取教师工作量失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取课程时间分布
     */
    @GetMapping("/course/time-distribution")
    @ApiOperation("获取课程时间分布")
    public ServerResponse getCourseTimeDistribution(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> distribution = dataAnalysisService.getCourseTimeDistribution(semester);
            return ServerResponse.ofSuccess(distribution);
        } catch (Exception e) {
            return ServerResponse.ofError("获取课程时间分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取冲突统计
     */
    @GetMapping("/conflict/statistics")
    @ApiOperation("获取冲突统计")
    public ServerResponse getConflictStatistics(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> conflicts = dataAnalysisService.getConflictStatistics(semester);
            return ServerResponse.ofSuccess(conflicts);
        } catch (Exception e) {
            return ServerResponse.ofError("获取冲突统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取完整的数据大屏数据
     */
    @GetMapping("/dashboard")
    @ApiOperation("获取数据大屏完整数据")
    public ServerResponse getDashboardData(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 汇总所有数据
            dashboard.put("overview", dataAnalysisService.getSystemOverview(semester));
            dashboard.put("scheduleStatistics", dataAnalysisService.getScheduleStatistics(semester));
            dashboard.put("classroomUtilization", dataAnalysisService.getClassroomUtilization(semester));
            dashboard.put("teacherWorkload", dataAnalysisService.getTeacherWorkload(semester));
            dashboard.put("timeDistribution", dataAnalysisService.getCourseTimeDistribution(semester));
            dashboard.put("conflictStatistics", dataAnalysisService.getConflictStatistics(semester));
            
            return ServerResponse.ofSuccess(dashboard);
        } catch (Exception e) {
            return ServerResponse.ofError("获取数据大屏数据失败：" + e.getMessage());
        }
    }
}
