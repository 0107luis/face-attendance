package com.facial.recognition.controller;

import com.facial.recognition.pojo.Teacher;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {
    
    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private AccessControlService accessControlService;
    
    // 获取所有教师（学生不允许访问）
    @GetMapping
    public ResponseEntity<?> getAllTeachers(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("获取所有教师请求, userId={}", userId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(userId);
            List<Teacher> teachers = teacherService.listAll();
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            logger.warn("获取教师列表失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    // 根据用户ID获取教师信息（学生不允许访问）
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTeacherByUserId(
            @PathVariable Integer userId,
            @RequestHeader(value = "X-User-Id", required = false) Integer requesterId) {
        logger.info("获取教师信息请求, targetUserId={}, requesterId={}", userId, requesterId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(requesterId);
            return teacherService.findByUserId(userId)
                    .map(teacher -> ResponseEntity.ok((Object) teacher))
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "教师不存在");
                        return ResponseEntity.status(404).body(error);
                    });
        } catch (Exception e) {
            logger.warn("获取教师信息失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    // 创建教师（学生不允许访问）
    @PostMapping
    public ResponseEntity<?> createTeacher(
            @RequestBody Teacher teacher,
            @RequestHeader(value = "X-User-Id", required = false) Integer requesterId) {
        logger.info("创建教师请求, requesterId={}", requesterId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(requesterId);
            Teacher savedTeacher = teacherService.createTeacher(teacher);
            return ResponseEntity.ok(savedTeacher);
        } catch (Exception e) {
            logger.warn("创建教师失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    // 更新教师信息（学生不允许访问）
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateTeacher(
            @PathVariable Integer userId,
            @RequestBody Teacher teacher,
            @RequestHeader(value = "X-User-Id", required = false) Integer requesterId) {
        logger.info("更新教师请求, targetUserId={}, requesterId={}", userId, requesterId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(requesterId);
            Teacher updatedTeacher = teacherService.updateTeacher(userId, teacher);
            return ResponseEntity.ok(updatedTeacher);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            logger.warn("更新教师失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    // 删除教师（学生不允许访问）
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteTeacher(
            @PathVariable Integer userId,
            @RequestHeader(value = "X-User-Id", required = false) Integer requesterId) {
        logger.info("删除教师请求, targetUserId={}, requesterId={}", userId, requesterId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(requesterId);
            teacherService.deleteByUserId(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("删除教师失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
}
