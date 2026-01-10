package com.facial.recognition.config;

import com.facial.recognition.exception.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理系统异常，提供友好的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数类型转换错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "参数类型错误");
        response.put("message", "参数 '" + e.getName() + "' 的值 '" + e.getValue() + "' 无法转换为正确的类型");
        response.put("details", "请检查参数格式是否正确");
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理数据完整性约束违反（外键约束等）
     */
    /**
     * 处理访问控制异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "权限不足");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "数据完整性错误");
        
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("foreign key constraint")) {
                response.put("message", "无法删除：该记录被其他数据引用，请先删除相关记录");
            } else if (message.contains("Duplicate entry")) {
                response.put("message", "数据重复：该记录已存在");
            } else {
                response.put("message", "数据完整性约束违反：" + message);
            }
        } else {
            response.put("message", "数据完整性约束违反");
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 处理IllegalStateException（业务逻辑错误）
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "业务逻辑错误");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 处理IllegalArgumentException（参数错误）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "参数错误");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理RuntimeException（运行时错误）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "运行时错误");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "系统错误");
        response.put("message", e.getMessage());
        e.printStackTrace(); // 记录详细错误日志
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

