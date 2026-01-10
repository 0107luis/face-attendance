package com.facial.recognition.controller;

import com.facial.recognition.pojo.AttendanceRecord;
import com.facial.recognition.pojo.Student;
import com.facial.recognition.service.AttendanceRecordService;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-records")
@CrossOrigin(origins = "*")
public class AttendanceRecordController {

    @Autowired
    private AttendanceRecordService recordService;
    
    @Autowired
    private AccessControlService accessControlService;
    
    @Autowired
    private StudentRepository studentRepository;

    // 返回补齐缺勤后的考勤记录
    @GetMapping("/my/full")
    public ResponseEntity<?> getMyAttendanceRecordsFull(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        try {
            if (userId == null) {
                throw new RuntimeException("未登录");
            }
            
            if (!accessControlService.isStudent(userId)) {
                throw new RuntimeException("仅学生可以访问此接口");
            }
            
            Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("找不到学生信息"));
                
            List<AttendanceRecord> records = recordService.findFullRecordsWithAbsent(student.getStudentId());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyAttendanceRecords(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        try {
            if (userId == null) {
                throw new RuntimeException("未登录");
            }
            
            if (!accessControlService.isStudent(userId)) {
                throw new RuntimeException("仅学生可以访问此接口");
            }
            
            Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("找不到学生信息"));
                
            List<AttendanceRecord> records = recordService.findByStudentId(student.getStudentId());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
}
