package com.chq.coursearrange.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 密码加密服务
 * 使用 BCrypt 算法进行密码加密和验证
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Service
public class PasswordService {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码（明文）
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码（明文）
     * @param encodedPassword 加密后的密码
     * @return 密码是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 检查密码强度
     * 
     * @param password 密码
     * @return 密码强度描述
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "弱：密码长度至少6位";
        }
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[^A-Za-z0-9].*")) strength++;
        
        if (strength >= 4) return "强";
        if (strength >= 2) return "中";
        return "弱";
    }
}
