package com.chq.coursearrange.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中的异常情况
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final int code;
    private final String message;
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.code = 1;  // 默认业务异常码
        this.message = message;
    }
    
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
