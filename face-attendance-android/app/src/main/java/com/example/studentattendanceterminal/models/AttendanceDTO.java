package com.example.studentattendanceterminal.models;

public class AttendanceDTO {
    private Long studentId;
    private Long courseId; // 如果没有具体任务，关联到课程
    private Long taskId;   // 如果有具体任务
    private Double latitude;
    private Double longitude;
    private String photoBase64; // 图片Base64字符串
    private Long checkInTime;
    private String status; // "正常" 或 "缺勤"

    public AttendanceDTO(Long studentId, Long courseId, Double latitude, Double longitude, String photoBase64, Long checkInTime, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoBase64 = photoBase64;
        this.checkInTime = checkInTime;
        this.status = status;
    }
    
    public AttendanceDTO(Long studentId, Long courseId, Long taskId, Double latitude, Double longitude, String photoBase64, Long checkInTime, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.taskId = taskId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoBase64 = photoBase64;
        this.checkInTime = checkInTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }
    
    public Long getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Long checkInTime) { this.checkInTime = checkInTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

