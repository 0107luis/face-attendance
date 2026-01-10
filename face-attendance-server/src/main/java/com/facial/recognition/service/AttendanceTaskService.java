package com.facial.recognition.service;

import com.facial.recognition.pojo.AttendanceTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceTaskService {
    
    // 创建考勤任务
    AttendanceTask createAttendanceTask(AttendanceTask attendanceTask);
    
    // 根据ID查找考勤任务
    Optional<AttendanceTask> findById(Long taskId);
    
    // 根据班级ID查找考勤任务
    List<AttendanceTask> findByCourseClassId(Long courseClassId);
    
    // 根据教师ID查找考勤任务
    List<AttendanceTask> findByTeacherId(Long teacherId);
    
    // 查找指定时间范围内的考勤任务
    List<AttendanceTask> findTasksInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    // 查找当前活跃的考勤任务
    List<AttendanceTask> findActiveTasksAtTime(LocalDateTime currentTime);
    
    // 查找即将开始的考勤任务
    List<AttendanceTask> findUpcomingTasks(LocalDateTime currentTime);
    
    // 更新考勤任务
    AttendanceTask updateAttendanceTask(Long taskId, AttendanceTask attendanceTask);
    
    // 删除考勤任务
    void deleteAttendanceTask(Long taskId);
    
    // 检查考勤任务是否正在进行
    boolean isTaskActive(Long taskId, LocalDateTime currentTime);
    
    // 检查考勤任务是否已过期
    boolean isTaskExpired(Long taskId, LocalDateTime currentTime);

    // 考勤统计相关方法

    // 统计某个班级的考勤任务数量
    Long getClassAttendanceTaskCount(Long courseClassId);

    // 统计某个教师的考勤任务数量
    Long getTeacherAttendanceTaskCount(Long teacherId);

    // 获取班级考勤任务统计详情
    java.util.Map<String, Object> getClassAttendanceTaskStatistics(Long courseClassId);

    // 获取教师考勤任务统计详情
    java.util.Map<String, Object> getTeacherAttendanceTaskStatistics(Long teacherId);
    
    // 获取班级考勤状况（某个考勤任务对应的所有学生考勤状态）
    java.util.List<com.facial.recognition.dto.ClassAttendanceStatusDTO> getClassAttendanceStatus(Long taskId);
}
