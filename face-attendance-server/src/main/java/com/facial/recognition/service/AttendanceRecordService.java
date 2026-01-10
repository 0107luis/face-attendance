package com.facial.recognition.service;

import com.facial.recognition.pojo.AttendanceRecord;
import java.util.List;

public interface AttendanceRecordService {
    List<AttendanceRecord> findByStudentId(Long studentId);

    AttendanceRecord save(AttendanceRecord record);

    /**
     * 获取学生考勤记录，包含已结束任务但未签到的缺勤记录
     */
    List<AttendanceRecord> findFullRecordsWithAbsent(Long studentId);
}
