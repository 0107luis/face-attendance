package com.attendance.service.impl;

import com.attendance.dto.AttendanceStatistics;
import com.attendance.entity.AttendanceRecord;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.StatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Override
    public AttendanceStatistics getUserStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = attendanceRecordMapper.findByUserIdAndDateRange(userId, startDate, endDate);
        
        AttendanceStatistics stats = new AttendanceStatistics();
        stats.setTotalDays(records.size());
        
        int normalDays = 0;
        int abnormalDays = 0;
        int lateDays = 0;
        int earlyLeaveDays = 0;
        int totalWorkMinutes = 0;
        
        for (AttendanceRecord record : records) {
            if ("normal".equals(record.getStatus())) {
                normalDays++;
            } else {
                abnormalDays++;
                if (record.getCheckInTime() != null) {
                    lateDays++;
                }
                if (record.getCheckOutTime() != null) {
                    earlyLeaveDays++;
                }
            }
            
            if (record.getWorkDuration() != null) {
                totalWorkMinutes += record.getWorkDuration();
            }
        }
        
        stats.setNormalDays(normalDays);
        stats.setAbnormalDays(abnormalDays);
        stats.setLateDays(lateDays);
        stats.setEarlyLeaveDays(earlyLeaveDays);
        stats.setTotalWorkMinutes(totalWorkMinutes);
        stats.setAverageWorkMinutes(records.size() > 0 ? totalWorkMinutes / records.size() : 0);
        stats.setAttendanceRate(records.size() > 0 ? (float) normalDays / records.size() * 100 : 0f);
        
        return stats;
    }

    @Override
    public List<AttendanceStatistics.DailyStatistics> getDailyStatistics(LocalDate startDate, LocalDate endDate) {
        List<AttendanceStatistics.DailyStatistics> dailyList = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<AttendanceRecord> records = attendanceRecordMapper.findByUserIdAndDateRange(null, current, current);
            
            AttendanceStatistics.DailyStatistics daily = new AttendanceStatistics.DailyStatistics();
            daily.setDate(current.format(DateTimeFormatter.ISO_DATE));
            daily.setCheckInCount((int) records.stream().filter(r -> r.getCheckInTime() != null).count());
            daily.setCheckOutCount((int) records.stream().filter(r -> r.getCheckOutTime() != null).count());
            daily.setNormalCount((int) records.stream().filter(r -> "normal".equals(r.getStatus())).count());
            daily.setAbnormalCount((int) records.stream().filter(r -> "abnormal".equals(r.getStatus())).count());
            
            dailyList.add(daily);
            current = current.plusDays(1);
        }
        
        return dailyList;
    }

    @Override
    public Map<String, Object> getDepartmentStatistics(String department, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        List<AttendanceRecord> records = attendanceRecordMapper.findByUserIdAndDateRange(null, startDate, endDate);
        if (department != null) {
            records = records.stream()
                .filter(record -> department.equals(record.getDepartment()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        long totalEmployees = records.stream().map(AttendanceRecord::getUserId).distinct().count();
        long normalCount = records.stream().filter(r -> "normal".equals(r.getStatus())).count();
        
        result.put("totalEmployees", totalEmployees);
        result.put("totalRecords", records.size());
        result.put("normalCount", normalCount);
        result.put("abnormalCount", records.size() - normalCount);
        result.put("attendanceRate", records.size() > 0 ? (float) normalCount / records.size() * 100 : 0f);
        
        return result;
    }

    @Override
    public List<AttendanceStatistics.MonthlyTrend> getMonthlyTrend(int months) {
        List<AttendanceStatistics.MonthlyTrend> trends = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate endDate = LocalDate.now().minusMonths(i).withDayOfMonth(1).plusMonths(1).minusDays(1);
            LocalDate startDate = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            
            AttendanceStatistics stats = getUserStatistics(null, startDate, endDate);
            
            AttendanceStatistics.MonthlyTrend trend = new AttendanceStatistics.MonthlyTrend();
            trend.setMonth(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            trend.setTotalDays(stats.getTotalDays());
            trend.setNormalDays(stats.getNormalDays());
            trend.setAttendanceRate(stats.getAttendanceRate());
            
            trends.add(trend);
        }
        
        return trends;
    }

    @Override
    public Map<String, Object> getRealTimeOverview() {
        Map<String, Object> result = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> todayRecords = attendanceRecordMapper.findByUserIdAndDateRange(null, today, today);
        
        long checkedInCount = todayRecords.stream().filter(r -> r.getCheckInTime() != null).count();
        long checkedOutCount = todayRecords.stream().filter(r -> r.getCheckOutTime() != null).count();
        
        result.put("date", today.format(DateTimeFormatter.ISO_DATE));
        result.put("checkedInCount", checkedInCount);
        result.put("checkedOutCount", checkedOutCount);
        result.put("notCheckedInCount", 0);
        result.put("realtimeList", todayRecords);
        
        return result;
    }
}