package com.facial.recognition.controller;

import com.facial.recognition.pojo.CourseClass;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.service.CourseClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-classes")
@CrossOrigin(origins = "*")
public class CourseClassController {

    @Autowired
    private CourseClassService courseClassService;
    
    @Autowired
    private AccessControlService accessControlService;

    // 创建班级
    @PostMapping
    public ResponseEntity<CourseClass> createCourseClass(@RequestBody CourseClass courseClass) {
        try {
            CourseClass createdClass = courseClassService.createCourseClass(courseClass);
            return ResponseEntity.ok(createdClass);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据ID获取班级
    @GetMapping("/{id}")
    public ResponseEntity<CourseClass> getCourseClassById(@PathVariable Long id) {
        Optional<CourseClass> courseClass = courseClassService.findById(id);
        return courseClass.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // 根据班级名称获取班级
    @GetMapping("/name/{className}")
    public ResponseEntity<List<CourseClass>> getCourseClassesByName(@PathVariable String className) {
        List<CourseClass> courseClasses = courseClassService.findByClassName(className);
        return ResponseEntity.ok(courseClasses);
    }

    // 获取所有班级
    @GetMapping
    public ResponseEntity<List<CourseClass>> getAllCourseClasses() {
        List<CourseClass> courseClasses = courseClassService.findAll();
        return ResponseEntity.ok(courseClasses);
    }

    // 根据课程ID获取班级
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseClass>> getCourseClassesByCourseId(@PathVariable Long courseId) {
        List<CourseClass> courseClasses = courseClassService.findByCourseId(courseId);
        return ResponseEntity.ok(courseClasses);
    }

    // 根据教师ID获取班级
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseClass>> getCourseClassesByTeacherId(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long teacherId) {
        accessControlService.assertCanAccessTeacherCourse(requesterUserId, teacherId);
        List<CourseClass> courseClasses = courseClassService.findByTeacherId(teacherId);
        return ResponseEntity.ok(courseClasses);
    }

    // 根据课程ID和教师ID获取班级
    @GetMapping("/course/{courseId}/teacher/{teacherId}")
    public ResponseEntity<List<CourseClass>> getCourseClassesByCourseAndTeacher(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long courseId, @PathVariable Long teacherId) {
        accessControlService.assertCanAccessTeacherCourse(requesterUserId, teacherId);
        List<CourseClass> courseClasses = courseClassService.findByCourseIdAndTeacherId(courseId, teacherId);
        return ResponseEntity.ok(courseClasses);
    }

    // 更新班级信息
    @PutMapping("/{id}")
    public ResponseEntity<CourseClass> updateCourseClass(
            @PathVariable Long id, @RequestBody CourseClass courseClass) {
        try {
            CourseClass updatedClass = courseClassService.updateCourseClass(id, courseClass);
            return ResponseEntity.ok(updatedClass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 删除班级
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseClass(@PathVariable Long id) {
        try {
            courseClassService.deleteCourseClass(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 移除已废弃的接口：status, updateStudentCount, date-range 等
}
