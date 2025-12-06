package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.dao.TeacherDao;
import com.chq.coursearrange.entity.Teacher;
import com.chq.coursearrange.service.PasswordService;
import com.chq.coursearrange.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CHQ
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {

    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private PasswordService passwordService;

    @Override
    public Teacher teacherLogin(String username, String password) {
        // 先根据教师编号查询教师
        QueryWrapper<Teacher> wrapper = new QueryWrapper();
        wrapper.eq("teacher_no", username);
        Teacher teacher = teacherDao.selectOne(wrapper);
        
        // 验证密码（支持BCrypt加密密码和明文密码的兼容）
        if (teacher != null) {
            // 如果密码以 $2a$ 或 $2b$ 开头，说明是BCrypt加密的密码
            if (teacher.getPassword().startsWith("$2a$") || teacher.getPassword().startsWith("$2b$")) {
                // 使用BCrypt验证
                if (passwordService.matches(password, teacher.getPassword())) {
                    return teacher;
                }
            } else {
                // 兼容旧的明文密码（后续需要迁移）
                if (password.equals(teacher.getPassword())) {
                    return teacher;
                }
            }
        }
        
        return null;
    }

    @Override
    public List<Teacher> selectTeacherAll() {
        List<Teacher> teacherList = teacherDao.selectList(null);
        return teacherList;
    }

    @Override
    public Teacher selectByRealName(String substring) {
        QueryWrapper<Teacher> qw = new QueryWrapper<>();
        qw.eq("realName", substring);
        Teacher teacher = teacherDao.selectOne(qw);
        return teacher;
    }

    @Override
    public int getSize() {
        int teacherSize = teacherDao.getSize();
        return teacherSize;
    }

    @Override
    public int getTeacherReg(String yesday) {
        int teacherReg = teacherDao.teacherReg(yesday);
        return teacherReg;
    }
}
