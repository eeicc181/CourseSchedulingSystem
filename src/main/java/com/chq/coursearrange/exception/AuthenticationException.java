package com.chq.coursearrange.exception;

import com.chq.coursearrange.common.ResponseCode;

/**
 * 认证异常
 * 用于用户认证失败的情况
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(ResponseCode.UNAUTHORIZED.getCode(), message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(ResponseCode.UNAUTHORIZED.getCode(), message, cause);
    }
}
