package com.facial.recognition.controller;

import com.facial.recognition.pojo.Student;
import com.facial.recognition.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 获取所有学生
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }
    
    // 分页获取学生
    @GetMapping("/paged")
    public ResponseEntity<?> getAllStudentsPaged(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "分页参数不合法");
            return ResponseEntity.badRequest().body(error);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> paged = studentService.findAllPaged(pageable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", paged.getContent());
        resp.put("totalElements", paged.getTotalElements());
        resp.put("totalPages", paged.getTotalPages());
        resp.put("page", paged.getNumber());
        resp.put("size", paged.getSize());
        return ResponseEntity.ok(resp);
    }

    // 根据ID获取学生
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Student> student = studentService.findById(id);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 根据学号获取学生
    @GetMapping("/number/{studentNumber}")
    public ResponseEntity<Student> getStudentByNumber(@PathVariable String studentNumber) {
        Optional<Student> student = studentService.findByStudentNumber(studentNumber);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 根据班级获取学生列表
    @GetMapping("/class/{className}")
    public ResponseEntity<List<Student>> getStudentsByClass(@PathVariable String className) {
        List<Student> students = studentService.findByClassName(className);
        return ResponseEntity.ok(students);
    }

    // 创建学生
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        try {
            Student created = studentService.createStudent(student);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 更新学生信息
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Student updated = studentService.updateStudent(id, student);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build(); // Conflict
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 删除学生
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        if (id == null || id <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无效的学生ID");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            studentService.deleteStudent(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "学生删除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(409).body(response); // Conflict
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // 根据班级名称模糊搜索
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String className) {
        List<Student> students = studentService.findByClassNameContaining(className);
        return ResponseEntity.ok(students);
    }
}

