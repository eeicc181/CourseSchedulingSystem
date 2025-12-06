package com.chq.coursearrange.controller;

import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.Exam;
import com.chq.coursearrange.service.ExamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考试管理控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/exam")
@Api(tags = "考试安排管理")
@CrossOrigin(origins = "*")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    /**
     * 创建考试
     */
    @PostMapping("/create")
    @ApiOperation("创建考试安排")
    public ServerResponse createExam(@RequestBody Exam exam) {
        try {
            boolean result = examService.createExam(exam);
            if (result) {
                return ServerResponse.ofSuccess("创建成功");
            } else {
                return ServerResponse.ofError("创建失败");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 自动分配考场
     */
    @PutMapping("/assign-classroom/{examId}")
    @ApiOperation("自动分配考场")
    public ServerResponse autoAssignClassroom(
            @ApiParam("考试ID") @PathVariable Integer examId) {
        try {
            boolean result = examService.autoAssignClassroom(examId);
            if (result) {
                return ServerResponse.ofSuccess("分配成功");
            } else {
                return ServerResponse.ofError("分配失败");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("分配失败：" + e.getMessage());
        }
    }
    
    /**
     * 自动分配监考教师
     */
    @PutMapping("/assign-invigilators/{examId}")
    @ApiOperation("自动分配监考教师")
    public ServerResponse autoAssignInvigilators(
            @ApiParam("考试ID") @PathVariable Integer examId,
            @ApiParam("监考教师数量") @RequestParam(defaultValue = "2") Integer count) {
        try {
            boolean result = examService.autoAssignInvigilators(examId, count);
            if (result) {
                return ServerResponse.ofSuccess("分配成功");
            } else {
                return ServerResponse.ofError("分配失败");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("分配失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成准考证号
     */
    @PostMapping("/generate-admission/{examId}")
    @ApiOperation("生成准考证号")
    public ServerResponse generateAdmissionNumbers(
            @ApiParam("考试ID") @PathVariable Integer examId) {
        try {
            boolean result = examService.generateAdmissionNumbers(examId);
            if (result) {
                return ServerResponse.ofSuccess("生成成功");
            } else {
                return ServerResponse.ofError("生成失败");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("生成失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取考试列表
     */
    @GetMapping("/list")
    @ApiOperation("获取考试列表")
    public ServerResponse getExamList(
            @ApiParam("学期") @RequestParam(required = false) String semester,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        try {
            List<Exam> exams = examService.getExamList(semester, status);
            return ServerResponse.ofSuccess(exams);
        } catch (Exception e) {
            return ServerResponse.ofError("获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取考试详情
     */
    @GetMapping("/detail/{examId}")
    @ApiOperation("获取考试详情")
    public ServerResponse getExamDetail(
            @ApiParam("考试ID") @PathVariable Integer examId) {
        try {
            Exam exam = examService.getById(examId);
            if (exam != null) {
                return ServerResponse.ofSuccess(exam);
            } else {
                return ServerResponse.ofError("考试不存在");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取考试统计
     */
    @GetMapping("/statistics")
    @ApiOperation("获取考试统计")
    public ServerResponse getExamStatistics(
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> stats = examService.getExamStatistics(semester);
            return ServerResponse.ofSuccess(stats);
        } catch (Exception e) {
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新考试状态
     */
    @PutMapping("/status/{examId}")
    @ApiOperation("更新考试状态")
    public ServerResponse updateExamStatus(
            @ApiParam("考试ID") @PathVariable Integer examId,
            @ApiParam("状态") @RequestParam String status) {
        try {
            Exam exam = examService.getById(examId);
            if (exam == null) {
                return ServerResponse.ofError("考试不存在");
            }
            
            exam.setStatus(status);
            boolean result = examService.updateById(exam);
            if (result) {
                return ServerResponse.ofSuccess("更新成功");
            } else {
                return ServerResponse.ofError("更新失败");
            }
        } catch (Exception e) {
            return ServerResponse.ofError("更新失败：" + e.getMessage());
        }
    }
}
