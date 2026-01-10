package com.example.studentattendanceterminal.models;

public class FaceRecognitionRequest {
    private String faceImage;
    private Integer userId;

    public FaceRecognitionRequest(String faceImage) {
        this.faceImage = faceImage;
    }

    public FaceRecognitionRequest(String faceImage, Integer userId) {
        this.faceImage = faceImage;
        this.userId = userId;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

