package com.facial.recognition.service.impl;

import com.facial.recognition.dto.StudentCourseDTO;
import com.facial.recognition.pojo.Course;
import com.facial.recognition.pojo.CourseClass;
import com.facial.recognition.pojo.StudentCourseClass;
import com.facial.recognition.repository.CourseClassRepository;
import com.facial.recognition.repository.CourseRepository;
import com.facial.recognition.repository.StudentCourseClassRepository;
import com.facial.recognition.service.StudentCourseClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentCourseClassServiceImpl implements StudentCourseClassService {

    @Autowired
    private StudentCourseClassRepository studentCourseClassRepository;
    
    @Autowired
    private CourseClassRepository courseClassRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public StudentCourseClass enrollStudent(Long studentId, Long classId) {
        // 检查是否已经选课
        Optional<StudentCourseClass> existing = studentCourseClassRepository
            .findByStudentIdAndClassId(studentId, classId);
        
        if (existing.isPresent()) {
            StudentCourseClass enrollment = existing.get();
            // 如果已退课（状态为0），则重新选课（更新状态为1）
            if (enrollment.getStatus() == 0) {
                enrollment.setStatus(1);
                enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
                return studentCourseClassRepository.save(enrollment);
            }
            // 如果状态为1，说明已经选过了
            throw new RuntimeException("Student is already enrolled in this course class");
        }
        
        StudentCourseClass enrollment = new StudentCourseClass(studentId, classId);
        return studentCourseClassRepository.save(enrollment);
    }

    @Override
    public StudentCourseClass dropCourse(Long studentId, Long classId) {
        Optional<StudentCourseClass> enrollment = studentCourseClassRepository
            .findByStudentIdAndClassId(studentId, classId);
        
        if (enrollment.isPresent()) {
            enrollment.get().setStatus(0);
            return studentCourseClassRepository.save(enrollment.get());
        }
        throw new RuntimeException("Enrollment not found");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentCourseClass> findById(Long id) {
        return studentCourseClassRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentCourseClass> findByStudentIdAndClassId(Long studentId, Long classId) {
        return studentCourseClassRepository.findByStudentIdAndClassId(studentId, classId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findByStudentId(Long studentId) {
        return studentCourseClassRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findByClassId(Long classId) {
        return studentCourseClassRepository.findByClassId(classId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findByStatus(Integer status) {
        return studentCourseClassRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findByStudentIdAndStatus(Long studentId, Integer status) {
        return studentCourseClassRepository.findByStudentIdAndStatus(studentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findByClassIdAndStatus(Long classId, Integer status) {
        return studentCourseClassRepository.findByClassIdAndStatus(classId, status);
    }

    @Override
    public StudentCourseClass updateStatus(Long id, Integer status) {
        Optional<StudentCourseClass> enrollment = studentCourseClassRepository.findById(id);
        if (enrollment.isPresent()) {
            enrollment.get().setStatus(status);
            return studentCourseClassRepository.save(enrollment.get());
        }
        throw new RuntimeException("Enrollment not found with id: " + id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countEnrolledStudentsByClassId(Long classId) {
        return studentCourseClassRepository.countEnrolledStudentsByClassId(classId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findCompletedCoursesByStudentId(Long studentId) {
        return studentCourseClassRepository.findCompletedCoursesByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> findEnrolledCoursesByStudentId(Long studentId) {
        return studentCourseClassRepository.findEnrolledCoursesByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long classId) {
        Optional<StudentCourseClass> enrollment = studentCourseClassRepository
            .findByStudentIdAndClassId(studentId, classId);
        return enrollment.isPresent() && Integer.valueOf(1).equals(enrollment.get().getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseClass> getStudentEnrollmentHistory(Long studentId) {
        return studentCourseClassRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<StudentCourseClass> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId) {
        return studentCourseClassRepository.findByStudentIdAndClassId(studentId, courseClassId);
    }

    @Override
    public List<StudentCourseClass> findByCourseClassId(Long courseClassId) {
        return studentCourseClassRepository.findByClassId(courseClassId);
    }

    @Override
    public List<StudentCourseClass> findByCourseClassIdAndStatus(Long courseClassId, Integer status) {
        return studentCourseClassRepository.findByClassIdAndStatus(courseClassId, status);
    }

    @Override
    public Long countEnrolledStudentsByCourseClassId(Long courseClassId) {
        return studentCourseClassRepository.countEnrolledStudentsByClassId(courseClassId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseDTO> getStudentCourses(Long studentId) {
        // 查询学生选修的所有班级记录（只查询状态为1正常的）
        List<StudentCourseClass> allEnrollments = studentCourseClassRepository.findByStudentIdAndStatus(studentId, 1);
        
        List<StudentCourseDTO> result = new ArrayList<>();
        
        for (StudentCourseClass enrollment : allEnrollments) {
            // 获取班级信息
            Optional<CourseClass> courseClassOpt = courseClassRepository.findById(enrollment.getClassId());
            if (courseClassOpt.isEmpty()) {
                continue;
            }

            CourseClass courseClass = courseClassOpt.get();

            // 获取课程信息
            Optional<Course> courseOpt = courseRepository.findById(courseClass.getCourseId());
            if (courseOpt.isEmpty()) {
                continue;
            }

            Course course = courseOpt.get();

            // 创建DTO，包含更多课程信息
            StudentCourseDTO dto = new StudentCourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseCode(),
                courseClass.getClassId(),
                courseClass.getClassName(),
                course.getCredits(),
                courseClass.getSchedule(),
                courseClass.getClassroom(),
                courseClass.getSemester()
            );

            result.add(dto);
        }
        
        return result;
    }

    @Override
    public List<StudentCourseClass> batchEnrollStudent(Long studentId, List<Long> classIds) {
        List<StudentCourseClass> enrollments = new ArrayList<>();

        for (Long classId : classIds) {
            try {
                // 检查是否已选课
                if (isStudentEnrolled(studentId, classId)) {
                    continue; // 已选课，跳过
                }

                // 检查时间冲突
                if (hasTimeConflict(studentId, classId)) {
                    continue; // 有冲突，跳过
                }

                // 检查容量是否已满
                if (isClassFull(classId)) {
                    continue; // 容量已满，跳过
                }

                // 执行选课
                StudentCourseClass enrollment = enrollStudent(studentId, classId);
                enrollments.add(enrollment);
            } catch (Exception e) {
                // 记录错误但继续处理其他课程
                System.err.println("Failed to enroll student " + studentId + " in class " + classId + ": " + e.getMessage());
            }
        }

        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasTimeConflict(Long studentId, Long classId) {
        // 获取要选的课程班级信息
        Optional<CourseClass> targetClass = courseClassRepository.findById(classId);
        if (targetClass.isEmpty()) {
            return false;
        }

        // 获取学生已选的所有课程
        List<StudentCourseClass> enrolledCourses = findEnrolledCoursesByStudentId(studentId);

        // 检查时间冲突（这里简化处理，假设CourseClass有时间字段）
        // 实际实现需要根据CourseClass的时间字段进行比较
        // 这里暂时返回false，表示没有冲突
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClassFull(Long classId) {
        // 检查班级容量限制
        // 这里需要CourseClass实体有capacity字段
        // 暂时假设没有容量限制
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseDTO> getAvailableCoursesForStudent(Long studentId) {
        // 获取所有课程班级
        List<CourseClass> allClasses = courseClassRepository.findAll();
        List<StudentCourseDTO> availableCourses = new ArrayList<>();

        // 获取学生已选课程的班级ID
        List<Long> enrolledClassIds = findEnrolledCoursesByStudentId(studentId)
            .stream()
            .map(StudentCourseClass::getClassId)
            .collect(Collectors.toList());

        for (CourseClass courseClass : allClasses) {
            // 跳过已选的课程
            if (enrolledClassIds.contains(courseClass.getClassId())) {
                continue;
            }

            // 获取课程信息
            Optional<Course> course = courseRepository.findById(courseClass.getCourseId());
            if (course.isPresent()) {
                StudentCourseDTO dto = new StudentCourseDTO(
                    course.get().getCourseId(),
                    course.get().getCourseName(),
                    course.get().getCourseCode(),
                    courseClass.getClassId(),
                    courseClass.getClassName(),
                    course.get().getCredits(),
                    courseClass.getSchedule(),
                    courseClass.getClassroom(),
                    courseClass.getSemester()
                );
                availableCourses.add(dto);
            }
        }

        return availableCourses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseDTO> getRecommendedCourses(Long studentId) {
        // 简单的推荐逻辑：推荐同类型或相关课程
        // 这里暂时返回所有可用课程作为推荐
        return getAvailableCoursesForStudent(studentId);
    }
}
