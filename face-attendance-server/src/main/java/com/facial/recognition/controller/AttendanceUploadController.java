package com.facial.recognition.controller;

import com.facial.recognition.dto.AttendanceUploadDTO;
import com.facial.recognition.exception.AccessDeniedException;
import com.facial.recognition.pojo.AttendanceRecord;
import com.facial.recognition.pojo.Student;
import com.facial.recognition.repository.StudentRepository;
import com.facial.recognition.service.AccessControlService;
import com.facial.recognition.service.AttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收前端签到记录上传（Studentattendanceterminal 调用）
 */
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceUploadController {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/record")
    public ResponseEntity<?> uploadAttendance(@RequestHeader("X-User-Id") Integer userId,
                                              @RequestBody AttendanceUploadDTO dto) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (userId == null) {
                throw new AccessDeniedException("缺少用户身份");
            }
            if (dto.getStudentId() == null) {
                throw new IllegalArgumentException("studentId 不能为空");
            }

            // 校验访问权限：必须是该学生本人或管理员
            accessControlService.assertCanAccessStudentCourse(userId, dto.getStudentId());

            // 确认学生存在
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));

            AttendanceRecord record = new AttendanceRecord();
            record.setStudentId(student.getStudentId());
            record.setTaskId(dto.getTaskId());
            if (dto.getCheckInTime() != null) {
                LocalDateTime ts = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(dto.getCheckInTime()), ZoneId.systemDefault());
                record.setCheckInTime(ts);
                record.setCreatedDate(ts);
            } else {
                record.setCreatedDate(LocalDateTime.now());
            }
            if (dto.getLatitude() != null) {
                record.setActualLatitude(BigDecimal.valueOf(dto.getLatitude()));
            }
            if (dto.getLongitude() != null) {
                record.setActualLongitude(BigDecimal.valueOf(dto.getLongitude()));
            }
            // 不存储照片，保留结果状态
            record.setAttendanceResult(dto.getStatus() == null ? "正常" : dto.getStatus());
            record.setRemark("Mobile check-in");

            AttendanceRecord saved = attendanceRecordService.save(record);
            resp.put("success", true);
            resp.put("recordId", saved.getRecordId());
            return ResponseEntity.ok(resp);
        } catch (AccessDeniedException ex) {
            resp.put("success", false);
            resp.put("message", ex.getMessage());
            return ResponseEntity.status(403).body(resp);
        } catch (IllegalArgumentException ex) {
            resp.put("success", false);
            resp.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("message", "保存考勤记录失败");
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}

