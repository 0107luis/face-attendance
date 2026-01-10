package com.facial.recognition.pojo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "AttendanceRecord")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID")
    private Long recordId;

    @Column(name = "TaskID", nullable = false)
    private Long taskId;

    @Column(name = "StudentID", nullable = false)
    private Long studentId;

    @Column(name = "CheckInTime", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "ActualLatitude", precision = 10, scale = 7)
    private BigDecimal actualLatitude;

    @Column(name = "ActualLongitude", precision = 10, scale = 7)
    private BigDecimal actualLongitude;

    @Column(name = "VerificationPhotoURL", length = 255)
    private String verificationPhotoUrl;

    @Column(name = "ConfidenceScore", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "AttendanceResult", nullable = false, length = 20)
    private String attendanceResult; // 正常/迟到/早退/缺勤/请假

    @Column(name = "Remark", length = 200)
    private String remark;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    public AttendanceRecord() {}

    // Getters and Setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public BigDecimal getActualLatitude() { return actualLatitude; }
    public void setActualLatitude(BigDecimal actualLatitude) { this.actualLatitude = actualLatitude; }

    public BigDecimal getActualLongitude() { return actualLongitude; }
    public void setActualLongitude(BigDecimal actualLongitude) { this.actualLongitude = actualLongitude; }

    public String getVerificationPhotoUrl() { return verificationPhotoUrl; }
    public void setVerificationPhotoUrl(String verificationPhotoUrl) { this.verificationPhotoUrl = verificationPhotoUrl; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getAttendanceResult() { return attendanceResult; }
    public void setAttendanceResult(String attendanceResult) { this.attendanceResult = attendanceResult; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}

