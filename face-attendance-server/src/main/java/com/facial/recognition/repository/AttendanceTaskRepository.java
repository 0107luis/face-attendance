package com.facial.recognition.repository;

import com.facial.recognition.pojo.AttendanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceTaskRepository extends JpaRepository<AttendanceTask, Long> {
    
    // 根据班级ID查找考勤任务
    List<AttendanceTask> findByCourseClassId(Long courseClassId);
    
    // 注意：TeacherID 在 AttendanceTask 表中不存在，需要关联 CourseClass 表查询
    // 这里简化处理，如果需要根据 TeacherID 查，建议在 Service 层先查 CourseClass ID 列表再查 Task
    // 或者使用 @Query 关联查询
    @Query("SELECT at FROM AttendanceTask at WHERE at.courseClassId IN (SELECT cc.classId FROM CourseClass cc WHERE cc.teacherId = :teacherId)")
    List<AttendanceTask> findByTeacherId(@Param("teacherId") Long teacherId);
    
    // 状态相关的查询需要改为基于时间判断，或者在应用层过滤
    // 这里暂时移除 findByStatus 等方法，改为提供基于时间的查询
    
    // 查找指定时间范围内的考勤任务
    @Query("SELECT at FROM AttendanceTask at WHERE at.startTime <= :endTime AND at.endTime >= :startTime")
    List<AttendanceTask> findTasksInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                            @Param("endTime") LocalDateTime endTime);
    
    // 查找当前活跃的考勤任务 (根据时间判断)
    @Query("SELECT at FROM AttendanceTask at WHERE at.startTime <= :currentTime AND at.endTime >= :currentTime")
    List<AttendanceTask> findActiveTasksAtTime(@Param("currentTime") LocalDateTime currentTime);
    
    // 查找即将开始的考勤任务
    @Query("SELECT at FROM AttendanceTask at WHERE at.startTime > :currentTime ORDER BY at.startTime ASC")
    List<AttendanceTask> findUpcomingTasks(@Param("currentTime") LocalDateTime currentTime);

    // 统计方法
    Long countByCourseClassId(Long courseClassId);

    @Query("SELECT COUNT(at) FROM AttendanceTask at WHERE at.courseClassId IN (SELECT cc.classId FROM CourseClass cc WHERE cc.teacherId = :teacherId)")
    Long countByTeacherId(@Param("teacherId") Long teacherId);
    
    // 查找指定时间之后的任务，按开始时间降序排列
    List<AttendanceTask> findByStartTimeAfterOrderByStartTimeDesc(LocalDateTime startTime);
    
    // 查找指定时间范围内的任务
    List<AttendanceTask> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
