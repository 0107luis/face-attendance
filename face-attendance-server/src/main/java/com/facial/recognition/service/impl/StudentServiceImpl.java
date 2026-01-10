package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.Student;
import com.facial.recognition.repository.AttendanceRecordRepository;
import com.facial.recognition.repository.FaceDataRepository;
import com.facial.recognition.repository.StudentRepository;
import com.facial.recognition.repository.StudentCourseClassRepository;
import com.facial.recognition.repository.UserRepository;
import com.facial.recognition.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentCourseClassRepository studentCourseClassRepository;
    
    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    
    @Autowired
    private FaceDataRepository faceDataRepository;
    
    @Override
    public Student createStudent(Student student) {
        // 妫€鏌ュ鍙锋槸鍚﹀凡瀛樺湪
        studentRepository.findByStudentNumber(student.getStudentNumber())
            .ifPresent(s -> { throw new IllegalArgumentException("Student number already exists"); });
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByUserId(Integer userId) {
        return studentRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByStudentNumber(String studentNumber) {
        Optional<Student> student = studentRepository.findByStudentNumber(studentNumber);
        if (student.isPresent()) {
            return student;
        }
        // 尝试通过用户名查找对应的学生信息
        return userRepository.findByUserName(studentNumber)
                .flatMap(user -> studentRepository.findByUserId(user.getUserID()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Student> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByClassName(String className) {
        return studentRepository.findByClassName(className);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByClassNameContaining(String className) {
        return studentRepository.findByClassNameContaining(className);
    }

    @Override
    public Student updateStudent(Long studentId, Student student) {
        Optional<Student> existingStudent = studentRepository.findById(studentId);
        if (existingStudent.isPresent()) {
            Student updatedStudent = existingStudent.get();
            updatedStudent.setStudentNumber(student.getStudentNumber());
            updatedStudent.setClassName(student.getClassName());
            updatedStudent.setUserId(student.getUserId());
            return studentRepository.save(updatedStudent);
        }
        throw new RuntimeException("Student not found with id: " + studentId);
    }

    @Override
    public Student updateStudentByUserId(Integer userId, Student student) {
        Optional<Student> existingStudent = studentRepository.findByUserId(userId);
        if (existingStudent.isPresent()) {
            Student updatedStudent = existingStudent.get();
            updatedStudent.setStudentNumber(student.getStudentNumber());
            updatedStudent.setClassName(student.getClassName());
            return studentRepository.save(updatedStudent);
        }
        throw new RuntimeException("Student not found with userId: " + userId);
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        
        Student student = studentRepository.findById(studentId).get();
        Integer userId = student.getUserId();
        
        // 1. 删除考勤记录
        List<com.facial.recognition.pojo.AttendanceRecord> records = attendanceRecordRepository.findByStudentId(studentId);
        attendanceRecordRepository.deleteAll(records);
        
        // 2. 删除选课记录
        List<com.facial.recognition.pojo.StudentCourseClass> enrollments = studentCourseClassRepository.findByStudentId(studentId);
        studentCourseClassRepository.deleteAll(enrollments);
        
        // 3. 删除人脸数据
        if (userId != null) {
            faceDataRepository.deleteByUserId(userId);
        }
        
        // 4. 删除学生记录
        studentRepository.deleteById(studentId);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        studentRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByStudentNumber(String studentNumber) {
        studentRepository.deleteByStudentNumber(studentNumber);
    }}



