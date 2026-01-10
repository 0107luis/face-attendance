package com.facial.recognition.pojo;

import java.io.Serializable;
import java.util.Objects;

/**
 * 学生课程班级复合主键类
 */
public class StudentCourseClassId implements Serializable {
    private Long studentId;
    private Long classId;

    public StudentCourseClassId() {}

    public StudentCourseClassId(Long studentId, Long classId) {
        this.studentId = studentId;
        this.classId = classId;
    }

    public Long getStudentId() { 
        return studentId; 
    }
    
    public void setStudentId(Long studentId) { 
        this.studentId = studentId; 
    }

    public Long getClassId() { 
        return classId; 
    }
    
    public void setClassId(Long classId) { 
        this.classId = classId; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentCourseClassId that = (StudentCourseClassId) o;
        return Objects.equals(studentId, that.studentId) && 
               Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, classId);
    }
}

