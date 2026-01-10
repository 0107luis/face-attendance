package com.facial.recognition.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity       //实体类
@Table(name = "student")       //建立表格
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studentid")
    private Long studentId; // 主键

    @Column(name = "student_number", unique = true)
    private String studentNumber; // 学号

    @Column(name = "student_name", nullable = false)
    private String studentName; // 姓名

    @Column(name = "major_class")
    private String className; // 所属班级

    @Column(name = "userid", nullable = false)
    private Integer userId; // 外键，关联User表

    public Student() {}

    public Student(String studentNumber, String studentName, String className, Integer userId) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.className = className;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}


