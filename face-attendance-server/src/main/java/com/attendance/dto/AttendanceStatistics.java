package com.attendance.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AttendanceStatistics {
    private Integer totalDays;
    private Integer normalDays;
    private Integer abnormalDays;
    private Integer lateDays;
    private Integer earlyLeaveDays;
    private Integer missedDays;
    private Integer totalWorkMinutes;
    private Integer averageWorkMinutes;
    private Float attendanceRate;
    private List<DailyStatistics> dailyList;
    private Map<String, Integer> departmentStats;
    private List<MonthlyTrend> monthlyTrend;
    
    @Data
    public static class DailyStatistics {
        private String date;
        private Integer checkInCount;
        private Integer checkOutCount;
        private Integer normalCount;
        private Integer abnormalCount;
    }
    
    @Data
    public static class MonthlyTrend {
        private String month;
        private Integer totalDays;
        private Integer normalDays;
        private Float attendanceRate;
    }
}
