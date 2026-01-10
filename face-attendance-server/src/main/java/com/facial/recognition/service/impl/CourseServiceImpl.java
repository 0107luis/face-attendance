package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.Course;
import com.facial.recognition.repository.CourseRepository;
import com.facial.recognition.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> listAll() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findBySemester(String semester) {
        return courseRepository.findBySemester(semester);
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        existing.setCourseName(course.getCourseName());
        existing.setCourseCode(course.getCourseCode());
        existing.setCredits(course.getCredits());
        existing.setSemester(course.getSemester());
        return courseRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Course> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
}
