package com.chq.coursearrange.common;

import com.chq.coursearrange.exception.AuthenticationException;
import com.chq.coursearrange.exception.BusinessException;
import com.chq.coursearrange.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 全局异常处理器（增强版）
 * 
 * @author CourseArrange Team
 * @version 2.0.0
 * @since 2024-12-06
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ServerResponse handleBusinessException(BusinessException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("业务异常 [TraceId: {}] [URI: {}] [Method: {}] [Message: {}]",
                traceId, request.getRequestURI(), request.getMethod(), e.getMessage());
        return ServerResponse.ofError(e.getCode(), e.getMessage() + " (错误ID: " + traceId + ")", null);
    }
    
    /**
     * 认证异常处理
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ServerResponse handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("认证异常 [TraceId: {}] [URI: {}] [Message: {}]",
                traceId, request.getRequestURI(), e.getMessage());
        return ServerResponse.ofError(e.getCode(), e.getMessage() + " (错误ID: " + traceId + ")", null);
    }
    
    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ServerResponse handleValidationException(ValidationException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("参数校验异常 [TraceId: {}] [URI: {}] [Message: {}]",
                traceId, request.getRequestURI(), e.getMessage());
        return ServerResponse.ofError(e.getCode(), e.getMessage() + " (错误ID: " + traceId + ")", null);
    }
    
    /**
     * Spring Validation 异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ServerResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败 [TraceId: {}] [URI: {}] [Error: {}]",
                traceId, request.getRequestURI(), errorMsg);
        return ServerResponse.ofError(ResponseCode.VALIDATE_FAILED.getCode(),
                "参数校验失败: " + errorMsg + " (错误ID: " + traceId + ")", null);
    }
    
    /**
     * SQL 异常处理
     */
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ServerResponse handleSQLException(SQLException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("数据库异常 [TraceId: {}] [URI: {}] [Method: {}] [SQLState: {}]",
                traceId, request.getRequestURI(), request.getMethod(), e.getSQLState(), e);
        return ServerResponse.ofError("数据库操作失败，请联系管理员。错误ID: " + traceId);
    }
    
    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ServerResponse handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("空指针异常 [TraceId: {}] [URI: {}] [Method: {}]",
                traceId, request.getRequestURI(), request.getMethod(), e);
        return ServerResponse.ofError("系统异常，请联系管理员。错误ID: " + traceId);
    }
    
    /**
     * 系统异常处理（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ServerResponse handleException(Exception e, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("系统异常 [TraceId: {}] [URI: {}] [Method: {}] [Params: {}] [Exception: {}]",
                traceId, request.getRequestURI(), request.getMethod(),
                request.getQueryString(), e.getClass().getSimpleName(), e);
        return ServerResponse.ofError("系统异常，请联系管理员。错误ID: " + traceId);
    }
    
    /**
     * 生成追踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

}
