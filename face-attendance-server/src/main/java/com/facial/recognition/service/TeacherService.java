package com.facial.recognition.service;

import com.facial.recognition.pojo.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService {
    Teacher createTeacher(Teacher t);
    Optional<Teacher> findByUserId(Integer userId);
    Teacher updateTeacher(Integer userId, Teacher update);
    void deleteByUserId(Integer userId);
    List<Teacher> listAll();
}
