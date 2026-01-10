package com.facial.recognition.pojo;
import jakarta.persistence.*;

@Entity
@Table(name = "Teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TeacherID")
    private Integer teacherId;

    @Column(name = "JobTitle")
    private String jobTitle; // 职称

    @Column(name = "Department")
    private String department; // 所属部门

    @Column(name = "UserID", nullable = false)
    private Integer userId; // 关联 User 的 userId（外键）

    @Column(name = "CreatedDate")
    private java.time.LocalDateTime createdDate;

    public Teacher() {}

    public Teacher(String jobTitle, String department, Integer userId) {
        this.jobTitle = jobTitle;
        this.department = department;
        this.userId = userId;
        this.createdDate = java.time.LocalDateTime.now();
    }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public java.time.LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDateTime createdDate) { this.createdDate = createdDate; }
}


