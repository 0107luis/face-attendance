package com.facial.recognition.controller;

import com.facial.recognition.pojo.StudentCourseClass;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.service.StudentCourseClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/student-course-classes")
@CrossOrigin(origins = "*")
public class StudentCourseClassController {

    @Autowired
    private StudentCourseClassService studentCourseClassService;
    
    @Autowired
    private AccessControlService accessControlService;

    // 学生选课
    @PostMapping("/enroll")
    public ResponseEntity<StudentCourseClass> enrollStudent(
            @RequestParam Long studentId, @RequestParam Long courseClassId) {
        try {
            StudentCourseClass enrollment = studentCourseClassService.enrollStudent(studentId, courseClassId);
            return ResponseEntity.ok(enrollment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 学生退课
    @PostMapping("/drop")
    public ResponseEntity<StudentCourseClass> dropCourse(
            @RequestParam Long studentId, @RequestParam Long courseClassId) {
        try {
            StudentCourseClass enrollment = studentCourseClassService.dropCourse(studentId, courseClassId);
            return ResponseEntity.ok(enrollment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据ID获取选课记录（注意：StudentCourseClass使用复合主键，此方法可能无法正常工作）
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEnrollmentById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无效的ID参数");
            return ResponseEntity.badRequest().body(response);
        }
        // 由于StudentCourseClass使用复合主键，此方法可能无法正常工作
        // 建议使用根据学生ID和班级ID查询的方法
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "StudentCourseClass使用复合主键，请使用根据学生ID和班级ID查询的方法");
        return ResponseEntity.badRequest().body(response);
    }

    // 根据学生ID和班级ID获取选课记录
    @GetMapping("/student/{studentId}/class/{courseClassId}")
    public ResponseEntity<StudentCourseClass> getEnrollmentByStudentAndClass(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId, @PathVariable Long courseClassId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        Optional<StudentCourseClass> enrollment = studentCourseClassService
            .findByStudentIdAndCourseClassId(studentId, courseClassId);
        return enrollment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // 根据学生ID获取选课记录
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentCourseClass>> getEnrollmentsByStudentId(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<StudentCourseClass> enrollments = studentCourseClassService.findByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // 根据班级ID获取选课记录
    @GetMapping("/class/{courseClassId}")
    public ResponseEntity<List<StudentCourseClass>> getEnrollmentsByCourseClassId(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long courseClassId) {
        accessControlService.assertCanAccessCourseClass(requesterUserId, courseClassId);
        List<StudentCourseClass> enrollments = studentCourseClassService.findByCourseClassId(courseClassId);
        return ResponseEntity.ok(enrollments);
    }

    // 根据状态获取选课记录
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentCourseClass>> getEnrollmentsByStatus(@PathVariable Integer status) {
        List<StudentCourseClass> enrollments = studentCourseClassService.findByStatus(status);
        return ResponseEntity.ok(enrollments);
    }

    // 根据学生ID和状态获取选课记录
    @GetMapping("/student/{studentId}/status/{status}")
    public ResponseEntity<List<StudentCourseClass>> getEnrollmentsByStudentAndStatus(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId, @PathVariable Integer status) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<StudentCourseClass> enrollments = studentCourseClassService
            .findByStudentIdAndStatus(studentId, status);
        return ResponseEntity.ok(enrollments);
    }

    // 根据班级ID和状态获取选课记录
    @GetMapping("/class/{courseClassId}/status/{status}")
    public ResponseEntity<List<StudentCourseClass>> getEnrollmentsByClassAndStatus(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long courseClassId, @PathVariable Integer status) {
        accessControlService.assertCanAccessCourseClass(requesterUserId, courseClassId);
        List<StudentCourseClass> enrollments = studentCourseClassService
            .findByCourseClassIdAndStatus(courseClassId, status);
        return ResponseEntity.ok(enrollments);
    }

    // 更新选课状态（注意：StudentCourseClass使用复合主键，此方法需要studentId和classId）
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateEnrollmentStatus(
            @PathVariable Long id, @RequestParam Integer status) {
        if (id == null || id <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无效的ID参数");
            return ResponseEntity.badRequest().body(response);
        }
        if (status == null || (status != 0 && status != 1)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "状态参数无效");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            StudentCourseClass enrollment = studentCourseClassService.updateStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", enrollment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    // 统计班级中的学生数量
    @GetMapping("/class/{courseClassId}/count")
    public ResponseEntity<Long> countEnrolledStudents(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long courseClassId) {
        accessControlService.assertCanAccessCourseClass(requesterUserId, courseClassId);
        Long count = studentCourseClassService.countEnrolledStudentsByCourseClassId(courseClassId);
        return ResponseEntity.ok(count);
    }

    // 获取学生已完成的课程
    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<StudentCourseClass>> getCompletedCourses(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<StudentCourseClass> enrollments = studentCourseClassService
            .findCompletedCoursesByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // 获取学生正在进行的课程
    @GetMapping("/student/{studentId}/enrolled")
    public ResponseEntity<List<StudentCourseClass>> getEnrolledCourses(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<StudentCourseClass> enrollments = studentCourseClassService
            .findEnrolledCoursesByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // 检查学生是否已选课
    @GetMapping("/student/{studentId}/class/{courseClassId}/enrolled")
    public ResponseEntity<Boolean> isStudentEnrolled(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId, @PathVariable Long courseClassId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        boolean isEnrolled = studentCourseClassService.isStudentEnrolled(studentId, courseClassId);
        return ResponseEntity.ok(isEnrolled);
    }

    // 获取学生选课历史
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<StudentCourseClass>> getStudentEnrollmentHistory(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<StudentCourseClass> enrollments = studentCourseClassService
            .getStudentEnrollmentHistory(studentId);
        return ResponseEntity.ok(enrollments);
    }
    
    // 获取学生选修的课程列表（包含课程信息）
    @GetMapping("/student/{studentId}/courses")
    public ResponseEntity<List<com.facial.recognition.dto.StudentCourseDTO>> getStudentCourses(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<com.facial.recognition.dto.StudentCourseDTO> courses = studentCourseClassService.getStudentCourses(studentId);
        return ResponseEntity.ok(courses);
    }

    // 测试端点
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Student Course API is working!");
    }

    // 新增功能

    // 批量选课
    @PostMapping("/batch-enroll")
    public ResponseEntity<Map<String, Object>> batchEnrollStudent(
            @RequestParam Long studentId, @RequestParam List<Long> classIds) {
        try {
            List<StudentCourseClass> enrollments = studentCourseClassService.batchEnrollStudent(studentId, classIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("enrolledCount", enrollments.size());
            response.put("enrollments", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 检查选课时间冲突
    @GetMapping("/check-conflict")
    public ResponseEntity<Map<String, Object>> checkTimeConflict(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @RequestParam Long studentId, @RequestParam Long classId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        boolean hasConflict = studentCourseClassService.hasTimeConflict(studentId, classId);
        Map<String, Object> response = new HashMap<>();
        response.put("hasConflict", hasConflict);
        response.put("canEnroll", !hasConflict);
        return ResponseEntity.ok(response);
    }

    // 检查课程容量
    @GetMapping("/class/{classId}/capacity")
    public ResponseEntity<Map<String, Object>> checkClassCapacity(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long classId) {
        accessControlService.assertCanAccessCourseClass(requesterUserId, classId);
        boolean isFull = studentCourseClassService.isClassFull(classId);
        Long enrolledCount = studentCourseClassService.countEnrolledStudentsByCourseClassId(classId);
        Map<String, Object> response = new HashMap<>();
        response.put("isFull", isFull);
        response.put("enrolledCount", enrolledCount);
        response.put("canEnroll", !isFull);
        return ResponseEntity.ok(response);
    }

    // 获取学生可选课程列表
    @GetMapping("/student/{studentId}/available-courses")
    public ResponseEntity<List<com.facial.recognition.dto.StudentCourseDTO>> getAvailableCourses(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<com.facial.recognition.dto.StudentCourseDTO> courses = studentCourseClassService.getAvailableCoursesForStudent(studentId);
        return ResponseEntity.ok(courses);
    }

    // 获取课程推荐
    @GetMapping("/student/{studentId}/recommended-courses")
    public ResponseEntity<List<com.facial.recognition.dto.StudentCourseDTO>> getRecommendedCourses(
            @RequestHeader("X-User-Id") Integer requesterUserId,
            @PathVariable Long studentId) {
        accessControlService.assertCanAccessStudentCourse(requesterUserId, studentId);
        List<com.facial.recognition.dto.StudentCourseDTO> courses = studentCourseClassService.getRecommendedCourses(studentId);
        return ResponseEntity.ok(courses);
    }
}

