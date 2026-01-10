package com.facial.recognition.controller;

import com.facial.recognition.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AttendanceTaskRepository attendanceTaskRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long studentCount = studentRepository.count();
        long teacherCount = teacherRepository.count();
        long courseCount = courseRepository.count();
        
        double todayAttendanceRate = calculateTodayAttendanceRate();
        
        stats.put("studentCount", studentCount);
        stats.put("teacherCount", teacherCount);
        stats.put("courseCount", courseCount);
        stats.put("todayAttendanceRate", String.format("%.1f%%", todayAttendanceRate));
        
        return stats;
    }

    @GetMapping("/recent-records")
    public List<Map<String, Object>> getRecentRecords() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        var tasks = attendanceTaskRepository.findByStartTimeAfterOrderByStartTimeDesc(sevenDaysAgo);
        
        List<Map<String, Object>> records = new ArrayList<>();
        for (var task : tasks) {
            if (records.size() >= 10) break;
            
            Map<String, Object> record = new HashMap<>();
            record.put("courseName", task.getCourseClass() != null ? task.getCourseClass().getCourse().getCourseName() : "未知课程");
            record.put("className", task.getCourseClass() != null ? task.getCourseClass().getClassName() : "未知班级");
            record.put("time", task.getStartTime().toString().replace("T", " "));
            
            long totalStudents = task.getCourseClass() != null ? 
                task.getCourseClass().getStudentCourseClasses().size() : 0;
            long attendedStudents = attendanceRecordRepository.countByTaskId(task.getTaskId());
            
            record.put("total", totalStudents);
            record.put("attended", attendedStudents);
            
            double rate = totalStudents > 0 ? (attendedStudents * 100.0 / totalStudents) : 0;
            record.put("rate", String.format("%.1f%%", rate));
            
            records.add(record);
        }
        
        return records;
    }

    private double calculateTodayAttendanceRate() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        var todayTasks = attendanceTaskRepository.findByStartTimeBetween(startOfDay, endOfDay);
        
        if (todayTasks.isEmpty()) {
            return 0.0;
        }
        
        long totalExpected = 0;
        long totalAttended = 0;
        
        for (var task : todayTasks) {
            if (task.getCourseClass() != null) {
                long expected = task.getCourseClass().getStudentCourseClasses().size();
                long attended = attendanceRecordRepository.countByTaskId(task.getTaskId());
                totalExpected += expected;
                totalAttended += attended;
            }
        }
        
        return totalExpected > 0 ? (totalAttended * 100.0 / totalExpected) : 0.0;
    }
}
