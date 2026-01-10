package com.facial.recognition.service;

import com.facial.recognition.pojo.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> listAll();
    Optional<Course> findById(Long id);
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findBySemester(String semester);
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
    void deleteById(Long id);

    org.springframework.data.domain.Page<Course> findAllPaged(org.springframework.data.domain.Pageable pageable);
}
