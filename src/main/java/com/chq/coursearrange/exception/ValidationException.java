package com.chq.coursearrange.exception;

import com.chq.coursearrange.common.ResponseCode;

/**
 * 参数校验异常
 * 用于参数验证失败的情况
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(ResponseCode.VALIDATE_FAILED.getCode(), message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(ResponseCode.VALIDATE_FAILED.getCode(), message, cause);
    }
}
