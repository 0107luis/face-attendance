package com.example.studentattendanceterminal.models;

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

    public boolean isMatched() {
        return matched;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public Double getThreshold() {
        return threshold;
    }

    public String getMessage() {
        return message;
    }

    public String getDebugInfo() {
        return debugInfo;
    }
}

