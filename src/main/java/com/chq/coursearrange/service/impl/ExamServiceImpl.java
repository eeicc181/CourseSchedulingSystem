package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chq.coursearrange.dao.ExamDao;
import com.chq.coursearrange.dao.ExamStudentDao;
import com.chq.coursearrange.entity.*;
import com.chq.coursearrange.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 考试服务实现
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamDao, Exam> implements ExamService {
    
    @Autowired
    private ExamStudentDao examStudentDao;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private StudentService studentService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createExam(Exam exam) {
        try {
            exam.setStatus("SCHEDULED");
            exam.setCreateTime(LocalDateTime.now());
            exam.setDeleted(false);
            
            boolean result = this.save(exam);
            if (result) {
                log.info("创建考试成功: {}", exam.getExamName());
            }
            return result;
        } catch (Exception e) {
            log.error("创建考试失败", e);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoAssignClassroom(Integer examId) {
        try {
            Exam exam = this.getById(examId);
            if (exam == null) {
                return false;
            }
            
            // 查找可用教室
            List<Classroom> classrooms = classroomService.list();
            if (classrooms.isEmpty()) {
                return false;
            }
            
            // 简单分配第一个教室
            exam.setClassroomNo(classrooms.get(0).getClassroomNo());
            exam.setUpdateTime(LocalDateTime.now());
            
            return this.updateById(exam);
        } catch (Exception e) {
            log.error("自动分配考场失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoAssignInvigilators(Integer examId, Integer count) {
        try {
            Exam exam = this.getById(examId);
            if (exam == null) {
                return false;
            }
            
            // 查找可用教师
            List<Teacher> teachers = teacherService.list();
            if (teachers.size() < count) {
                count = teachers.size();
            }
            
            // 随机选择教师
            Collections.shuffle(teachers);
            List<String> teacherNos = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                teacherNos.add(teachers.get(i).getTeacherNo());
            }
            
            exam.setInvigilators(String.join(",", teacherNos));
            exam.setUpdateTime(LocalDateTime.now());
            
            return this.updateById(exam);
        } catch (Exception e) {
            log.error("自动分配监考教师失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateAdmissionNumbers(Integer examId) {
        try {
            Exam exam = this.getById(examId);
            if (exam == null) {
                return false;
            }
            
            // 获取参考班级的学生
            if (exam.getClassNos() == null || exam.getClassNos().isEmpty()) {
                return false;
            }
            
            String[] classNos = exam.getClassNos().split(",");
            List<ExamStudent> examStudents = new ArrayList<>();
            
            int seatNumber = 1;
            for (String classNo : classNos) {
                QueryWrapper<Student> wrapper = new QueryWrapper<>();
                wrapper.eq("class_no", classNo.trim());
                List<Student> students = studentService.list(wrapper);
                
                for (Student student : students) {
                    ExamStudent examStudent = new ExamStudent();
                    examStudent.setExamId(examId);
                    examStudent.setStudentNo(student.getStudentNo());
                    examStudent.setSeatNo(String.format("A%02d", seatNumber++));
                    examStudent.setAdmissionNo(generateAdmissionNo(exam, student));
                    examStudent.setAbsent(false);
                    examStudent.setDeleted(false);
                    
                    examStudents.add(examStudent);
                }
            }
            
            // 批量插入
            for (ExamStudent examStudent : examStudents) {
                examStudentDao.insert(examStudent);
            }
            
            log.info("生成准考证号成功，共{}人", examStudents.size());
            return true;
        } catch (Exception e) {
            log.error("生成准考证号失败", e);
            return false;
        }
    }
    
    private String generateAdmissionNo(Exam exam, Student student) {
        // 格式：学期(4位) + 考试ID(4位) + 学号后4位
        String semester = exam.getSemester().replace("-", "").substring(0, 4);
        String examIdStr = String.format("%04d", exam.getId());
        String studentSuffix = student.getStudentNo().substring(
            Math.max(0, student.getStudentNo().length() - 4));
        
        return semester + examIdStr + studentSuffix;
    }
    
    @Override
    public List<Exam> getExamList(String semester, String status) {
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();
        
        if (semester != null && !semester.isEmpty()) {
            wrapper.eq("semester", semester);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        
        wrapper.eq("deleted", false);
        wrapper.orderByDesc("exam_time");
        
        return this.list(wrapper);
    }
    
    @Override
    public Map<String, Object> getExamStatistics(String semester) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            QueryWrapper<Exam> wrapper = new QueryWrapper<>();
            if (semester != null && !semester.isEmpty()) {
                wrapper.eq("semester", semester);
            }
            wrapper.eq("deleted", false);
            
            List<Exam> exams = this.list(wrapper);
            
            int totalExams = exams.size();
            int scheduled = 0;
            int inProgress = 0;
            int completed = 0;
            int cancelled = 0;
            
            for (Exam exam : exams) {
                switch (exam.getStatus()) {
                    case "SCHEDULED": scheduled++; break;
                    case "IN_PROGRESS": inProgress++; break;
                    case "COMPLETED": completed++; break;
                    case "CANCELLED": cancelled++; break;
                    default: break;
                }
            }
            
            stats.put("totalExams", totalExams);
            stats.put("scheduled", scheduled);
            stats.put("inProgress", inProgress);
            stats.put("completed", completed);
            stats.put("cancelled", cancelled);
            
        } catch (Exception e) {
            log.error("获取考试统计失败", e);
        }
        
        return stats;
    }
}
