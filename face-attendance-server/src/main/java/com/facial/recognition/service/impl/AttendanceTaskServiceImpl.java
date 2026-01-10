package com.facial.recognition.service.impl;

import com.facial.recognition.dto.ClassAttendanceStatusDTO;
import com.facial.recognition.pojo.AttendanceRecord;
import com.facial.recognition.pojo.AttendanceTask;
import com.facial.recognition.pojo.Student;
import com.facial.recognition.pojo.StudentCourseClass;
import com.facial.recognition.repository.AttendanceRecordRepository;
import com.facial.recognition.repository.AttendanceTaskRepository;
import com.facial.recognition.repository.StudentCourseClassRepository;
import com.facial.recognition.repository.StudentRepository;
import com.facial.recognition.service.AttendanceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceTaskServiceImpl implements AttendanceTaskService {

    @Autowired
    private AttendanceTaskRepository attendanceTaskRepository;
    
    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    
    @Autowired
    private StudentCourseClassRepository studentCourseClassRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public AttendanceTask createAttendanceTask(AttendanceTask attendanceTask) {
        // 设置创建时间
        attendanceTask.setCreatedTime(LocalDateTime.now());
        // 不再生成二维码
        return attendanceTaskRepository.save(attendanceTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttendanceTask> findById(Long taskId) {
        return attendanceTaskRepository.findById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTask> findByCourseClassId(Long courseClassId) {
        return attendanceTaskRepository.findByCourseClassId(courseClassId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTask> findByTeacherId(Long teacherId) {
        return attendanceTaskRepository.findByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTask> findTasksInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return attendanceTaskRepository.findTasksInTimeRange(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTask> findActiveTasksAtTime(LocalDateTime currentTime) {
        return attendanceTaskRepository.findActiveTasksAtTime(currentTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceTask> findUpcomingTasks(LocalDateTime currentTime) {
        return attendanceTaskRepository.findUpcomingTasks(currentTime);
    }

    @Override
    public AttendanceTask updateAttendanceTask(Long taskId, AttendanceTask attendanceTask) {
        Optional<AttendanceTask> existingTask = attendanceTaskRepository.findById(taskId);
        if (existingTask.isPresent()) {
            AttendanceTask updatedTask = existingTask.get();
            // 更新字段
            if (attendanceTask.getStartTime() != null) {
                updatedTask.setStartTime(attendanceTask.getStartTime());
            }
            if (attendanceTask.getEndTime() != null) {
                updatedTask.setEndTime(attendanceTask.getEndTime());
            }
            if (attendanceTask.getLocationRange() != null) {
                updatedTask.setLocationRange(attendanceTask.getLocationRange());
            }
            if (attendanceTask.getLatitude() != null) {
                updatedTask.setLatitude(attendanceTask.getLatitude());
            }
            if (attendanceTask.getLongitude() != null) {
                updatedTask.setLongitude(attendanceTask.getLongitude());
            }
            if (attendanceTask.getRadius() != null) {
                updatedTask.setRadius(attendanceTask.getRadius());
            }
            
            return attendanceTaskRepository.save(updatedTask);
        }
        throw new RuntimeException("AttendanceTask not found with id: " + taskId);
    }

    @Override
    public void deleteAttendanceTask(Long taskId) {
        attendanceTaskRepository.deleteById(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTaskActive(Long taskId, LocalDateTime currentTime) {
        Optional<AttendanceTask> task = attendanceTaskRepository.findById(taskId);
        if (task.isPresent()) {
            AttendanceTask attendanceTask = task.get();
            return currentTime.isAfter(attendanceTask.getStartTime()) &&
                   currentTime.isBefore(attendanceTask.getEndTime());
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTaskExpired(Long taskId, LocalDateTime currentTime) {
        Optional<AttendanceTask> task = attendanceTaskRepository.findById(taskId);
        if (task.isPresent()) {
            return currentTime.isAfter(task.get().getEndTime());
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getClassAttendanceTaskCount(Long courseClassId) {
        return attendanceTaskRepository.countByCourseClassId(courseClassId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTeacherAttendanceTaskCount(Long teacherId) {
        return attendanceTaskRepository.countByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getClassAttendanceTaskStatistics(Long courseClassId) {
        List<AttendanceTask> tasks = attendanceTaskRepository.findByCourseClassId(courseClassId);
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalTasks", tasks.size());
        statistics.put("activeTasks", tasks.stream()
            .filter(task -> isTaskActive(task.getTaskId(), LocalDateTime.now()))
            .count());
        statistics.put("expiredTasks", tasks.stream()
            .filter(task -> isTaskExpired(task.getTaskId(), LocalDateTime.now()))
            .count());
        statistics.put("upcomingTasks", tasks.stream()
            .filter(task -> LocalDateTime.now().isBefore(task.getStartTime()))
            .count());

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTeacherAttendanceTaskStatistics(Long teacherId) {
        List<AttendanceTask> tasks = attendanceTaskRepository.findByTeacherId(teacherId);
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalTasks", tasks.size());
        statistics.put("activeTasks", tasks.stream()
            .filter(task -> isTaskActive(task.getTaskId(), LocalDateTime.now()))
            .count());
        statistics.put("expiredTasks", tasks.stream()
            .filter(task -> isTaskExpired(task.getTaskId(), LocalDateTime.now()))
            .count());
        statistics.put("upcomingTasks", tasks.stream()
            .filter(task -> LocalDateTime.now().isBefore(task.getStartTime()))
            .count());

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassAttendanceStatusDTO> getClassAttendanceStatus(Long taskId) {
        // 1. 获取考勤任务
        Optional<AttendanceTask> taskOpt = attendanceTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("考勤任务不存在: " + taskId);
        }
        
        AttendanceTask task = taskOpt.get();
        Long courseClassId = task.getCourseClassId();
        
        // 2. 获取该班级的所有学生（通过 StudentCourseClass，状态为正常的学生）
        // 注意：数据库 Status 是 TINYINT(1)，1表示正常，0表示退课
        List<StudentCourseClass> enrollments = studentCourseClassRepository
            .findByClassIdAndStatus(courseClassId, 1);
        
        // 3. 获取该任务的考勤记录
        List<AttendanceRecord> records = attendanceRecordRepository.findByTaskId(taskId);
        Map<Long, AttendanceRecord> recordMap = records.stream()
            .collect(Collectors.toMap(AttendanceRecord::getStudentId, r -> r));
        
        // 4. 构建返回列表：所有学生默认未签到，如果有考勤记录则更新状态
        return enrollments.stream().map(enrollment -> {
            Long studentId = enrollment.getStudentId();
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            
            if (!studentOpt.isPresent()) {
                // 如果学生不存在，跳过
                return null;
            }
            
            Student student = studentOpt.get();
            ClassAttendanceStatusDTO dto = new ClassAttendanceStatusDTO(
                student.getStudentId(),
                student.getStudentNumber(),
                student.getStudentName()
            );
            
            // 如果有考勤记录，更新状态
            AttendanceRecord record = recordMap.get(studentId);
            if (record != null) {
                dto.setAttendanceResult(record.getAttendanceResult());
                dto.setCheckInTime(record.getCheckInTime());
                dto.setRemark(record.getRemark());
            }
            
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }
}
