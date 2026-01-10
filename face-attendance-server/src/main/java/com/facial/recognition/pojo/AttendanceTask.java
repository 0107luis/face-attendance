package com.facial.recognition.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "AttendanceTask")
public class AttendanceTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskID")
    private Long taskId;

    @Column(name = "ClassID", nullable = false)
    private Long courseClassId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", referencedColumnName = "ClassID", insertable = false, updatable = false)
    private CourseClass courseClass;

    // 数据库中 AttendanceTask 表没有 TeacherID，这里先注释掉或设为Transient
    // 实际业务中可以通过 ClassID -> CourseClass -> TeacherID 获取
    @Transient
    private Long teacherId;

    @Column(name = "StartTime", nullable = false)
    private LocalDateTime startTime; // 考勤开始时间

    @Column(name = "EndTime", nullable = false)
    private LocalDateTime endTime; // 考勤结束时间

    @Column(name = "LocationRange")
    private String locationRange; // 定位范围(GPS坐标/半径)

    @Column(name = "Latitude", precision = 10, scale = 7)
    private BigDecimal latitude; // 纬度

    @Column(name = "Longitude", precision = 10, scale = 7)
    private BigDecimal longitude; // 经度

    @Column(name = "Radius")
    private Integer radius; // 有效范围半径(米)

    @Column(name = "CreatedDate")
    private LocalDateTime createdTime; // 创建时间

    // 为了兼容前端传递的 taskName，这里保留字段但不映射到数据库
    // 或者将其拼接到 description 中
    @Transient
    private String taskName; 

    // Status 和 QRCode 在数据库中不存在
    @Transient
    private String status; 
    
    @Transient
    private String qrCode;

    public AttendanceTask() {}

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getCourseClassId() { return courseClassId; }
    public void setCourseClassId(Long courseClassId) { this.courseClassId = courseClassId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocationRange() { return locationRange; }
    public void setLocationRange(String locationRange) { this.locationRange = locationRange; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getStatus() { 
        // 动态计算状态
        if (LocalDateTime.now().isBefore(startTime)) return "UPCOMING";
        if (LocalDateTime.now().isAfter(endTime)) return "EXPIRED";
        return "ACTIVE";
    }
    public void setStatus(String status) { this.status = status; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public CourseClass getCourseClass() { return courseClass; }
    public void setCourseClass(CourseClass courseClass) { this.courseClass = courseClass; }
}
