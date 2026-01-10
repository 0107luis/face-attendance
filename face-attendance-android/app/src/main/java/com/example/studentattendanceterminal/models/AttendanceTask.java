package com.example.studentattendanceterminal.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 考勤任务模型
 */
public class AttendanceTask {
    @SerializedName("taskId")
    private Long taskId;
    
    @SerializedName("courseClassId")
    private Long courseClassId;
    
    @SerializedName("startTime")
    private String startTime;
    
    @SerializedName("endTime")
    private String endTime;
    
    @SerializedName("locationRange")
    private String locationRange;
    
    @SerializedName("latitude")
    private Double latitude;
    
    @SerializedName("longitude")
    private Double longitude;
    
    @SerializedName("radius")
    private Integer radius;
    
    @SerializedName("status")
    private String status; // UPCOMING, ACTIVE, EXPIRED

    public AttendanceTask() {}

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCourseClassId() {
        return courseClassId;
    }

    public void setCourseClassId(Long courseClassId) {
        this.courseClassId = courseClassId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocationRange() {
        return locationRange;
    }

    public void setLocationRange(String locationRange) {
        this.locationRange = locationRange;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 检查任务是否处于活跃状态（当前时间在开始和结束时间之间）
     */
    public boolean isActive() {
        if (startTime == null || endTime == null) {
            return false;
        }
        try {
            long now = System.currentTimeMillis();
            // 解析ISO格式的时间字符串，支持多种格式
            java.text.SimpleDateFormat[] formats = {
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()),
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault()),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            };
            
            long start = 0;
            long end = 0;
            
            // 尝试解析开始时间
            String startStr = startTime.replace("Z", "").replaceAll("([+-]\\d{2}):(\\d{2})$", "$1$2");
            for (java.text.SimpleDateFormat sdf : formats) {
                try {
                    start = sdf.parse(startStr).getTime();
                    break;
                } catch (Exception ignored) {}
            }
            
            // 尝试解析结束时间
            String endStr = endTime.replace("Z", "").replaceAll("([+-]\\d{2}):(\\d{2})$", "$1$2");
            for (java.text.SimpleDateFormat sdf : formats) {
                try {
                    end = sdf.parse(endStr).getTime();
                    break;
                } catch (Exception ignored) {}
            }
            
            return start > 0 && end > 0 && now >= start && now <= end;
        } catch (Exception e) {
            // 如果解析失败，使用status字段判断
            return "ACTIVE".equals(status);
        }
    }
}

