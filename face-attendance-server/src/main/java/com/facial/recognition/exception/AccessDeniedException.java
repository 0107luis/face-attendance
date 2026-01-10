package com.facial.recognition.exception;

/**
 * 访问控制异常，在用户越权访问资源时抛出
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}

