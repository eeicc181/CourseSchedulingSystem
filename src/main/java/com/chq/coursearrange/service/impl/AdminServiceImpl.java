package com.chq.coursearrange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chq.coursearrange.dao.AdminDao;
import com.chq.coursearrange.entity.Admin;
import com.chq.coursearrange.service.AdminService;
import com.chq.coursearrange.service.PasswordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CHQ
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminDao, Admin> implements AdminService {

    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private PasswordService passwordService;

    @Override
    public Admin adminLogin(String username, String password) {
        // 先根据用户名查询管理员
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("admin_no", username);
        Admin admin = adminDao.selectOne(wrapper);
        
        // 验证密码（支持BCrypt加密密码和明文密码的兼容）
        if (admin != null) {
            // 如果密码以 $2a$ 或 $2b$ 开头，说明是BCrypt加密的密码
            if (admin.getPassword().startsWith("$2a$") || admin.getPassword().startsWith("$2b$")) {
                // 使用BCrypt验证
                if (passwordService.matches(password, admin.getPassword())) {
                    return admin;
                }
            } else {
                // 兼容旧的明文密码（后续需要迁移）
                if (password.equals(admin.getPassword())) {
                    return admin;
                }
            }
        }
        
        return null;
    }
}
