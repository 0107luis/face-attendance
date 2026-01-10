package com.facial.recognition.util;

/**
 * 参数验证工具类
 */
public class ValidationUtil {
    
    /**
     * 验证ID是否有效
     * @param id ID值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果ID无效
     */
    public static void validateId(Long id, String paramName) {
        if (id == null) {
            throw new IllegalArgumentException(paramName + " 不能为空");
        }
        if (id <= 0) {
            throw new IllegalArgumentException(paramName + " 必须大于0");
        }
    }
    
    /**
     * 验证ID是否有效（Integer类型）
     * @param id ID值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果ID无效
     */
    public static void validateId(Integer id, String paramName) {
        if (id == null) {
            throw new IllegalArgumentException(paramName + " 不能为空");
        }
        if (id <= 0) {
            throw new IllegalArgumentException(paramName + " 必须大于0");
        }
    }
    
    /**
     * 验证字符串是否为空
     * @param value 字符串值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果字符串为空
     */
    public static void validateNotBlank(String value, String paramName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " 不能为空");
        }
        if ("undefined".equalsIgnoreCase(value) || "null".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException(paramName + " 值无效: " + value);
        }
    }
}

