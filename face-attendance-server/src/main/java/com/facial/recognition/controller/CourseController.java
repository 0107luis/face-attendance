package com.facial.recognition.controller;

import com.facial.recognition.pojo.Course;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private AccessControlService accessControlService;
    
    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("获取所有课程请求, userId={}", userId);
        
        try {
            // 允许学生、教师、管理员访问
            if (!accessControlService.isStudent(userId) && !accessControlService.isTeacher(userId) && !accessControlService.isAdmin(userId)) {
                throw new RuntimeException("无权访问");
            }
            List<Course> courses = courseService.listAll();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            logger.warn("获取课程列表失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }

    // 分页获取课程
    @GetMapping("/paged")
    public ResponseEntity<?> getAllCoursesPaged(@RequestHeader(value = "X-User-Id", required = false) Integer userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        logger.info("分页获取课程请求, userId={}, page={}, size={}", userId, page, size);
        try {
            if (!accessControlService.isStudent(userId) && !accessControlService.isTeacher(userId) && !accessControlService.isAdmin(userId)) {
                throw new RuntimeException("无权访问");
            }
            if (page < 0 || size <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "分页参数不合法");
                return ResponseEntity.badRequest().body(error);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> paged = courseService.findAllPaged(pageable);
            Map<String, Object> resp = new HashMap<>();
            resp.put("content", paged.getContent());
            resp.put("totalElements", paged.getTotalElements());
            resp.put("totalPages", paged.getTotalPages());
            resp.put("page", paged.getNumber());
            resp.put("size", paged.getSize());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.warn("获取课程列表失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("获取课程详情请求, courseId={}, userId={}", id, userId);
        
        try {
            // 允许学生、教师、管理员访问
            if (!accessControlService.isStudent(userId) && !accessControlService.isTeacher(userId) && !accessControlService.isAdmin(userId)) {
                throw new RuntimeException("无权访问");
            }
            return courseService.findById(id)
                    .map(course -> ResponseEntity.ok((Object) course))
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "课程不存在");
                        return ResponseEntity.status(404).body(error);
                    });
        } catch (Exception e) {
            logger.warn("获取课程详情失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody Course course,
            @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("创建课程请求, userId={}", userId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(userId);
            Course savedCourse = courseService.createCourse(course);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            logger.warn("创建课程失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long id,
            @RequestBody Course course,
            @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("更新课程请求, courseId={}, userId={}", id, userId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(userId);
            Course updatedCourse = courseService.updateCourse(id, course);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            logger.warn("更新课程失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        logger.info("删除课程请求, courseId={}, userId={}", id, userId);
        
        try {
            accessControlService.assertCanAccessTeacherInfo(userId);
            courseService.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("删除课程失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
}
