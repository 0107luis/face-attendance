package com.facial.recognition.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CourseClass")
public class CourseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClassID")
    private Long classId; // 主键

    @Column(name = "ClassName", nullable = false)
    private String className; // 班级名称/代号

    @Column(name = "CourseID", nullable = false, insertable = false, updatable = false)
    private Long courseId; // 外键

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID", referencedColumnName = "CourseID")
    private Course course;

    @Column(name = "TeacherID", nullable = false)
    private Long teacherId; // 外键

    @JsonIgnore
    @OneToMany(mappedBy = "classId", fetch = FetchType.LAZY)
    private List<StudentCourseClass> studentCourseClasses;

    @Column(name = "ClassLocation")
    private String classroom; // 教室 (对应数据库 ClassLocation)

    @Column(name = "ClassTime")
    private String schedule; // 上课时间安排 (对应数据库 ClassTime)

    @Transient // 数据库中可能没有这个字段，或者需要核对 create_database.sql
    private Integer maxStudents; // 最大学生数

    @Transient // 数据库中可能没有这个字段
    private Integer currentStudents; // 当前学生数

    @Transient // 数据库中可能没有这个字段
    private LocalDateTime startDate; // 开课时间

    @Transient // 数据库中可能没有这个字段
    private LocalDateTime endDate; // 结课时间

    @Transient // 数据库中可能没有这个字段
    private String status; // 状态：ACTIVE, INACTIVE, COMPLETED
    
    @Column(name = "Semester")
    private String semester; // 学期

    public CourseClass() {}

    public CourseClass(String className, Long courseId, Long teacherId) {
        this.className = className;
        this.courseId = courseId;
        this.teacherId = teacherId;
        // this.status = "ACTIVE";
    }

    // Getters and Setters
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Integer getCurrentStudents() { return currentStudents; }
    public void setCurrentStudents(Integer currentStudents) { this.currentStudents = currentStudents; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public List<StudentCourseClass> getStudentCourseClasses() { return studentCourseClasses; }
    public void setStudentCourseClasses(List<StudentCourseClass> studentCourseClasses) { this.studentCourseClasses = studentCourseClasses; }
}
