package com.facial.recognition.dto;

import java.time.LocalDateTime;

/**
 * 班级考勤状况DTO
 * 用于返回某个考勤任务对应的班级所有学生的考勤状态
 */
public class ClassAttendanceStatusDTO {
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private String attendanceResult; // 考勤结果：未签到/正常/迟到/早退/缺勤/请假
    private LocalDateTime checkInTime; // 签到时间，如果未签到则为null
    private String remark; // 备注

    public ClassAttendanceStatusDTO() {}

    public ClassAttendanceStatusDTO(Long studentId, String studentNumber, String studentName) {
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.attendanceResult = "未签到"; // 默认未签到
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getAttendanceResult() { return attendanceResult; }
    public void setAttendanceResult(String attendanceResult) { this.attendanceResult = attendanceResult; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}

