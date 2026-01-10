package com.facial.recognition.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "Course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private Long courseId;

    @Column(name = "CourseName", nullable = false)
    private String courseName;

    @Column(name = "CourseCode", unique = true, nullable = false)
    private String courseCode; // 课程编号

    @Column(name = "Credits")
    private Double credits; // 学分

    @Column(name = "Semester")
    private String semester; // 开课学期，例如 2025-秋

    public Course() {}

    public Course(String courseName, String courseCode, Double credits, String semester) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
        this.semester = semester;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Double getCredits() { return credits; }
    public void setCredits(Double credits) { this.credits = credits; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}

