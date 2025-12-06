package com.chq.coursearrange.controller;

import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.CourseSelection;
import com.chq.coursearrange.service.CourseSelectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 选课控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/course-selection")
@Api(tags = "在线选课管理")
@CrossOrigin(origins = "*")
public class CourseSelectionController {
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    /**
     * 学生选课
     */
    @PostMapping("/select")
    @ApiOperation("学生选课")
    public ServerResponse selectCourse(
            @ApiParam("学号") @RequestParam String studentNo,
            @ApiParam("课程编号") @RequestParam String courseNo,
            @ApiParam("学期") @RequestParam String semester,
            @ApiParam("优先级") @RequestParam(defaultValue = "1") Integer priority) {
        try {
            boolean result = courseSelectionService.selectCourse(studentNo, courseNo, semester, priority);
            if (result) {
                return ServerResponse.ofSuccess("选课成功");
            } else {
                return ServerResponse.ofError("选课失败，可能已选过该课程");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("选课失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消选课
     */
    @DeleteMapping("/cancel/{selectionId}")
    @ApiOperation("取消选课")
    public ServerResponse cancelSelection(
            @ApiParam("选课记录ID") @PathVariable Integer selectionId) {
        try {
            boolean result = courseSelectionService.cancelSelection(selectionId);
            if (result) {
                return ServerResponse.ofSuccess("取消成功");
            } else {
                return ServerResponse.ofError("取消失败，记录不存在或已确认");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("取消失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取学生选课列表
     */
    @GetMapping("/student/{studentNo}")
    @ApiOperation("获取学生选课列表")
    public ServerResponse getStudentSelections(
            @ApiParam("学号") @PathVariable String studentNo,
            @ApiParam("学期") @RequestParam String semester) {
        try {
            List<CourseSelection> selections = courseSelectionService.getStudentSelections(studentNo, semester);
            return ServerResponse.ofSuccess(selections);
        } catch (Exception e) {
            return ServerResponse.ofError("获取选课列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取课程选课统计
     */
    @GetMapping("/stats/{courseNo}")
    @ApiOperation("获取课程选课统计")
    public ServerResponse getCourseStats(
            @ApiParam("课程编号") @PathVariable String courseNo,
            @ApiParam("学期") @RequestParam String semester) {
        try {
            Map<String, Object> stats = courseSelectionService.getCourseSelectionStats(courseNo, semester);
            return ServerResponse.ofSuccess(stats);
        } catch (Exception e) {
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
}
