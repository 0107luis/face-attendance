package com.facial.recognition.repository;

import com.facial.recognition.pojo.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    
    // 根据任务ID查找考勤记录
    List<AttendanceRecord> findByTaskId(Long taskId);
    
    // 根据学生ID查找考勤记录
    List<AttendanceRecord> findByStudentId(Long studentId);
    
    // 根据任务ID和学生ID查找考勤记录
    Optional<AttendanceRecord> findByTaskIdAndStudentId(Long taskId, Long studentId);
    
    // 根据任务ID查找所有考勤记录
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.taskId = :taskId")
    List<AttendanceRecord> findAllByTaskId(@Param("taskId") Long taskId);
    
    // 统计某个任务的考勤记录数量
    Long countByTaskId(Long taskId);
}

