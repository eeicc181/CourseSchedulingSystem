package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.dao.StudentDao;
import com.chq.coursearrange.entity.Student;
import com.chq.coursearrange.service.PasswordService;
import com.chq.coursearrange.service.StudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CHQ
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentDao, Student> implements StudentService {

    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private PasswordService passwordService;

    @Override
    public Student studentLogin(String username, String password) {
        // 先根据学号查询学生
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", username);
        Student student = studentDao.selectOne(wrapper);
        
        // 验证密码（支持BCrypt加密密码和明文密码的兼容）
        if (student != null) {
            // 如果密码以 $2a$ 或 $2b$ 开头，说明是BCrypt加密的密码
            if (student.getPassword().startsWith("$2a$") || student.getPassword().startsWith("$2b$")) {
                // 使用BCrypt验证
                if (passwordService.matches(password, student.getPassword())) {
                    return student;
                }
            } else {
                // 兼容旧的明文密码（后续需要迁移）
                if (password.equals(student.getPassword())) {
                    return student;
                }
            }
        }
        
        return null;
    }

    @Override
    public int getSize() {
        int studentSize = studentDao.getSize();
        return studentSize;
    }

    @Override
    public int getStudentReg(String yesday) {
        int studentReg = studentDao.studentReg(yesday);
        return studentReg;
    }

}
