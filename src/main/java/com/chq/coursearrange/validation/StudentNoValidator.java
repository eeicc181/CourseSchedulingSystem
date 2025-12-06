package com.chq.coursearrange.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 学号格式校验器
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class StudentNoValidator implements ConstraintValidator<StudentNo, String> {
    
    @Override
    public void initialize(StudentNo constraintAnnotation) {
        // 初始化方法，可以获取注解参数
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        // 学号格式：10位数字
        return value.matches("^\\d{10}$");
    }
}
