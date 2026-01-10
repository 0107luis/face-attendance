package com.facial.recognition.dto;

/**
 * 学生选修课程DTO
 * 用于返回学生选修的课程信息
 */
public class StudentCourseDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long classId;
    private String className;
    private Double credits; // 学分
    private String classTime; // 上课时间
    private String classLocation; // 上课地点
    private String semester; // 学期

    public StudentCourseDTO() {}

    public StudentCourseDTO(Long courseId, String courseName, String courseCode, Long classId, String className) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.classId = classId;
        this.className = className;
    }

    public StudentCourseDTO(Long courseId, String courseName, String courseCode, Long classId, String className,
                           Double credits, String classTime, String classLocation, String semester) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.classId = classId;
        this.className = className;
        this.credits = credits;
        this.classTime = classTime;
        this.classLocation = classLocation;
        this.semester = semester;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassLocation() {
        return classLocation;
    }

    public void setClassLocation(String classLocation) {
        this.classLocation = classLocation;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}

