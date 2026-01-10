package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.Teacher;
import com.facial.recognition.repository.FaceDataRepository;
import com.facial.recognition.repository.TeacherRepository;
import com.facial.recognition.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    
    @Autowired
    private FaceDataRepository faceDataRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository) { this.teacherRepository = teacherRepository; }

    @Override
    @Transactional
    public Teacher createTeacher(Teacher t) { return teacherRepository.save(t); }

    @Override
    public Optional<Teacher> findByUserId(Integer userId) { return teacherRepository.findByUserId(userId); }

    @Override
    @Transactional
    public Teacher updateTeacher(Integer userId, Teacher update) {
        Teacher t = teacherRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("teacher not found"));
        t.setDepartment(update.getDepartment()); t.setJobTitle(update.getJobTitle());
        return teacherRepository.save(t);
    }

    @Override
    @Transactional
    public void deleteByUserId(Integer userId) { 
        teacherRepository.findByUserId(userId).ifPresent(t -> {
            // 删除人脸数据
            faceDataRepository.deleteByUserId(userId);
            // 删除教师记录
            teacherRepository.delete(t);
        });
    }

    @Override
    public List<Teacher> listAll() { return teacherRepository.findAll(); }
}
