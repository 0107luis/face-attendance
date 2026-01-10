package com.facial.recognition.controller;

import com.facial.recognition.pojo.AttendanceTask;
import com.facial.recognition.service.AttendanceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-tasks")
@CrossOrigin(origins = "*")
public class AttendanceTaskController {

    @Autowired
    private AttendanceTaskService attendanceTaskService;

    // 创建考勤任务
    @PostMapping
    public ResponseEntity<AttendanceTask> createAttendanceTask(@RequestBody AttendanceTask attendanceTask) {
        try {
            AttendanceTask createdTask = attendanceTaskService.createAttendanceTask(attendanceTask);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据ID获取考勤任务
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceTask> getAttendanceTaskById(@PathVariable Long id) {
        Optional<AttendanceTask> task = attendanceTaskService.findById(id);
        return task.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // 根据班级ID获取考勤任务
    @GetMapping("/class/{courseClassId}")
    public ResponseEntity<List<AttendanceTask>> getTasksByCourseClassId(@PathVariable Long courseClassId) {
        List<AttendanceTask> tasks = attendanceTaskService.findByCourseClassId(courseClassId);
        return ResponseEntity.ok(tasks);
    }

    // 根据教师ID获取考勤任务
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<AttendanceTask>> getTasksByTeacherId(@PathVariable Long teacherId) {
        List<AttendanceTask> tasks = attendanceTaskService.findByTeacherId(teacherId);
        return ResponseEntity.ok(tasks);
    }

    // 根据时间范围获取考勤任务
    @GetMapping("/time-range")
    public ResponseEntity<List<AttendanceTask>> getTasksInTimeRange(
            @RequestParam String startTime, @RequestParam String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            List<AttendanceTask> tasks = attendanceTaskService.findTasksInTimeRange(start, end);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取当前活跃的考勤任务
    @GetMapping("/active")
    public ResponseEntity<List<AttendanceTask>> getActiveTasks() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<AttendanceTask> tasks = attendanceTaskService.findActiveTasksAtTime(currentTime);
        return ResponseEntity.ok(tasks);
    }

    // 获取即将开始的考勤任务
    @GetMapping("/upcoming")
    public ResponseEntity<List<AttendanceTask>> getUpcomingTasks() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<AttendanceTask> tasks = attendanceTaskService.findUpcomingTasks(currentTime);
        return ResponseEntity.ok(tasks);
    }

    // 更新考勤任务
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceTask> updateAttendanceTask(
            @PathVariable Long id, @RequestBody AttendanceTask attendanceTask) {
        try {
            AttendanceTask updatedTask = attendanceTaskService.updateAttendanceTask(id, attendanceTask);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 删除考勤任务
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendanceTask(@PathVariable Long id) {
        try {
            attendanceTaskService.deleteAttendanceTask(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 检查考勤任务是否正在进行
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isTaskActive(@PathVariable Long id) {
        LocalDateTime currentTime = LocalDateTime.now();
        boolean isActive = attendanceTaskService.isTaskActive(id, currentTime);
        return ResponseEntity.ok(isActive);
    }

    // 检查考勤任务是否已过期
    @GetMapping("/{id}/expired")
    public ResponseEntity<Boolean> isTaskExpired(@PathVariable Long id) {
        LocalDateTime currentTime = LocalDateTime.now();
        boolean isExpired = attendanceTaskService.isTaskExpired(id, currentTime);
        return ResponseEntity.ok(isExpired);
    }

    // 考勤统计相关接口

    // 获取班级考勤任务数量统计
    @GetMapping("/class/{courseClassId}/count")
    public ResponseEntity<Long> getClassAttendanceTaskCount(@PathVariable Long courseClassId) {
        Long count = attendanceTaskService.getClassAttendanceTaskCount(courseClassId);
        return ResponseEntity.ok(count);
    }

    // 获取教师考勤任务数量统计
    @GetMapping("/teacher/{teacherId}/count")
    public ResponseEntity<Long> getTeacherAttendanceTaskCount(@PathVariable Long teacherId) {
        Long count = attendanceTaskService.getTeacherAttendanceTaskCount(teacherId);
        return ResponseEntity.ok(count);
    }

    // 获取班级考勤任务统计详情
    @GetMapping("/class/{courseClassId}/statistics")
    public ResponseEntity<Map<String, Object>> getClassAttendanceTaskStatistics(@PathVariable Long courseClassId) {
        Map<String, Object> statistics = attendanceTaskService.getClassAttendanceTaskStatistics(courseClassId);
        return ResponseEntity.ok(statistics);
    }

    // 获取教师考勤任务统计详情
    @GetMapping("/teacher/{teacherId}/statistics")
    public ResponseEntity<Map<String, Object>> getTeacherAttendanceTaskStatistics(@PathVariable Long teacherId) {
        Map<String, Object> statistics = attendanceTaskService.getTeacherAttendanceTaskStatistics(teacherId);
        return ResponseEntity.ok(statistics);
    }

    // 获取班级考勤状况（某个考勤任务对应的所有学生考勤状态）
    @GetMapping("/{taskId}/attendance-status")
    public ResponseEntity<List<com.facial.recognition.dto.ClassAttendanceStatusDTO>> getClassAttendanceStatus(
            @PathVariable Long taskId) {
        try {
            List<com.facial.recognition.dto.ClassAttendanceStatusDTO> statusList = 
                attendanceTaskService.getClassAttendanceStatus(taskId);
            return ResponseEntity.ok(statusList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
