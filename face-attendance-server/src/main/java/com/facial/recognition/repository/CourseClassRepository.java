package com.facial.recognition.repository;

import com.facial.recognition.pojo.CourseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {
    
    // 根据课程ID查找班级
    List<CourseClass> findByCourseId(Long courseId);
    
    // 根据教师ID查找班级
    List<CourseClass> findByTeacherId(Long teacherId);
    
    // 根据班级名称查找班级
    List<CourseClass> findByClassName(String className);
    
    // 根据课程ID和教师ID查找班级
    List<CourseClass> findByCourseIdAndTeacherId(Long courseId, Long teacherId);
    
    // 根据教室查找班级 (对应实体属性 classroom)
    List<CourseClass> findByClassroom(String classroom);
    
    // 移除不兼容的 status, startDate, endDate 查询
}
