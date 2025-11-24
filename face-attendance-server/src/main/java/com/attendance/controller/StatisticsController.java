package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.dto.AttendanceStatistics;
import com.attendance.service.AntiCheatService;
import com.attendance.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private AntiCheatService antiCheatService;

    @GetMapping("/user/{userId}")
    public Result<AttendanceStatistics> getUserStatistics(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        AttendanceStatistics stats = statisticsService.getUserStatistics(userId, startDate, endDate);
        return Result.success(stats);
    }

    @GetMapping("/daily")
    public Result<List<AttendanceStatistics.DailyStatistics>> getDailyStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<AttendanceStatistics.DailyStatistics> dailyList = statisticsService.getDailyStatistics(startDate, endDate);
        return Result.success(dailyList);
    }

    @GetMapping("/department")
    public Result<Map<String, Object>> getDepartmentStatistics(
            @RequestParam(required = false) String department,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = statisticsService.getDepartmentStatistics(department, startDate, endDate);
        return Result.success(stats);
    }

    @GetMapping("/monthly")
    public Result<List<AttendanceStatistics.MonthlyTrend>> getMonthlyTrend(
            @RequestParam(defaultValue = "6") int months) {
        List<AttendanceStatistics.MonthlyTrend> trends = statisticsService.getMonthlyTrend(months);
        return Result.success(trends);
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> getRealTimeOverview() {
        Map<String, Object> overview = statisticsService.getRealTimeOverview();
        return Result.success(overview);
    }

    @GetMapping("/risk-check")
    public Result<Map<String, Object>> checkRisk(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String ipAddress,
            @RequestParam String location,
            @RequestParam String checkType) {
        Map<String, Object> riskResult = antiCheatService.checkRisk(userId, deviceId, ipAddress, location, checkType);
        return Result.success(riskResult);
    }
}
