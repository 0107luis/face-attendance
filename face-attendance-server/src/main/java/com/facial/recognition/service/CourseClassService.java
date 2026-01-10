package com.facial.recognition.service;

import com.facial.recognition.pojo.CourseClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseClassService {
    
    // 创建班级
    CourseClass createCourseClass(CourseClass courseClass);
    
    // 根据ID查找班级
    Optional<CourseClass> findById(Long courseClassId);
    
    // 根据班级名称查找班级
    List<CourseClass> findByClassName(String className);
    
    // 获取所有班级
    List<CourseClass> findAll();
    
    // 根据课程ID查找班级
    List<CourseClass> findByCourseId(Long courseId);
    
    // 根据教师ID查找班级
    List<CourseClass> findByTeacherId(Long teacherId);
    
    // 根据课程ID和教师ID查找班级
    List<CourseClass> findByCourseIdAndTeacherId(Long courseId, Long teacherId);
    
    // 更新班级信息
    CourseClass updateCourseClass(Long courseClassId, CourseClass courseClass);
    
    // 删除班级
    void deleteCourseClass(Long courseClassId);
}
