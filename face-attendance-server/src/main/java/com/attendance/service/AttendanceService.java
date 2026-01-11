package com.attendance.service;

import com.attendance.entity.AttendanceRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService extends IService<AttendanceRecord> {
    
    Map<String, Object> checkIn(Long userId, String photo, Float liveness, String location, String deviceInfo);
    
    Map<String, Object> checkOut(Long userId, String photo, Float liveness, String location, String deviceInfo);
    
    AttendanceRecord findByUserIdAndDate(Long userId, LocalDate date);
    
    IPage<AttendanceRecord> getPageRecords(int current, int size, LocalDate startDate, LocalDate endDate, Long userId);
    
    List<AttendanceRecord> getUserRecords(Long userId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate);
}
