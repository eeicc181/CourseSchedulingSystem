package com.chq.coursearrange.controller;

import com.chq.coursearrange.common.ServerResponse;
import com.chq.coursearrange.entity.Score;
import com.chq.coursearrange.service.ScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/score")
@Api(tags = "成绩管理")
@CrossOrigin(origins = "*")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    @PostMapping("/input")
    @ApiOperation("录入成绩")
    public ServerResponse inputScore(@RequestBody Score score) {
        try {
            boolean result = scoreService.inputScore(score);
            return result ? ServerResponse.ofSuccess("录入成功") : ServerResponse.ofError("录入失败");
        } catch (Exception e) {
            return ServerResponse.ofError("录入失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/batch-input")
    @ApiOperation("批量录入成绩")
    public ServerResponse batchInputScore(@RequestBody List<Score> scores) {
        try {
            boolean result = scoreService.batchInputScore(scores);
            return result ? ServerResponse.ofSuccess("批量录入成功") : ServerResponse.ofError("批量录入失败");
        } catch (Exception e) {
            return ServerResponse.ofError("批量录入失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/student/{studentNo}")
    @ApiOperation("获取学生成绩")
    public ServerResponse getStudentScores(
            @ApiParam("学号") @PathVariable String studentNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            List<Score> scores = scoreService.getStudentScores(studentNo, semester);
            return ServerResponse.ofSuccess(scores);
        } catch (Exception e) {
            return ServerResponse.ofError("获取失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/course/{courseNo}")
    @ApiOperation("获取课程成绩")
    public ServerResponse getCourseScores(
            @ApiParam("课程编号") @PathVariable String courseNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            List<Score> scores = scoreService.getCourseScores(courseNo, semester);
            return ServerResponse.ofSuccess(scores);
        } catch (Exception e) {
            return ServerResponse.ofError("获取失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/statistics/{courseNo}")
    @ApiOperation("获取成绩统计")
    public ServerResponse getScoreStatistics(
            @ApiParam("课程编号") @PathVariable String courseNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> stats = scoreService.getScoreStatistics(courseNo, semester);
            return ServerResponse.ofSuccess(stats);
        } catch (Exception e) {
            return ServerResponse.ofError("获取统计失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/transcript/{studentNo}")
    @ApiOperation("生成成绩单")
    public ServerResponse generateTranscript(
            @ApiParam("学号") @PathVariable String studentNo,
            @ApiParam("学期") @RequestParam(required = false) String semester) {
        try {
            Map<String, Object> transcript = scoreService.generateTranscript(studentNo, semester);
            return ServerResponse.ofSuccess(transcript);
        } catch (Exception e) {
            return ServerResponse.ofError("生成成绩单失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    @ApiOperation("更新成绩")
    public ServerResponse updateScore(@RequestBody Score score) {
        try {
            scoreService.calculateTotalScore(score);
            boolean result = scoreService.updateById(score);
            return result ? ServerResponse.ofSuccess("更新成功") : ServerResponse.ofError("更新失败");
        } catch (Exception e) {
            return ServerResponse.ofError("更新失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{scoreId}")
    @ApiOperation("删除成绩")
    public ServerResponse deleteScore(@ApiParam("成绩ID") @PathVariable Integer scoreId) {
        try {
            Score score = scoreService.getById(scoreId);
            if (score == null) {
                return ServerResponse.ofError("成绩不存在");
            }
            
            score.setDeleted(true);
            boolean result = scoreService.updateById(score);
            return result ? ServerResponse.ofSuccess("删除成功") : ServerResponse.ofError("删除失败");
        } catch (Exception e) {
            return ServerResponse.ofError("删除失败：" + e.getMessage());
        }
    }
}
