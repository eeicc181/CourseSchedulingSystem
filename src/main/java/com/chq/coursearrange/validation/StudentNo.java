package com.chq.coursearrange.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 学号格式校验注解
 * 学号格式：10位数字
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StudentNoValidator.class)
@Documented
public @interface StudentNo {
    
    String message() default "学号格式不正确，应为10位数字";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
