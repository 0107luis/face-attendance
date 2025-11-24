package com.attendance.service;

import com.attendance.dto.AttendanceStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    
    AttendanceStatistics getUserStatistics(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<AttendanceStatistics.DailyStatistics> getDailyStatistics(LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getDepartmentStatistics(String department, LocalDate startDate, LocalDate endDate);
    
    List<AttendanceStatistics.MonthlyTrend> getMonthlyTrend(int months);
    
    Map<String, Object> getRealTimeOverview();
}