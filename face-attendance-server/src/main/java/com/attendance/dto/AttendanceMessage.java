package com.attendance.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AttendanceMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private Long userId;
    private String checkType;
    private String photo;
    private Float liveness;
    private String location;
    private String latitude;
    private String longitude;
    private String deviceInfo;
    private String deviceId;
    private String ipAddress;
    private LocalDateTime timestamp;
    private Integer retryCount;
    
    public AttendanceMessage() {
        this.timestamp = LocalDateTime.now();
        this.retryCount = 0;
    }
}
