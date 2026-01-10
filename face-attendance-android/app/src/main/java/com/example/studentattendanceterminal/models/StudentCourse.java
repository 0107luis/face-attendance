package com.example.studentattendanceterminal.models;

/**
 * 学生选修课程模型
 */
public class StudentCourse {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Long classId;
    private String className;

    public StudentCourse() {}

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
    
    @Override
    public String toString() {
        return courseName != null ? courseName : "";
    }
}

