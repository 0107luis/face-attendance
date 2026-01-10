package com.facial.recognition.dto;

/**
 * 人脸识别结果
 */
public class FaceRecognitionResult {
    private boolean hasFace;
    private boolean matched;
    private Integer userId;
    private String userName;
    private Double similarity;
    private Double threshold;
    private String message;
    private String debugInfo;

    public boolean isHasFace() {
        return hasFace;
    }

    public void setHasFace(boolean hasFace) {
        this.hasFace = hasFace;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }
}

