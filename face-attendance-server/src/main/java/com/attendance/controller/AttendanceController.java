package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.AttendanceRecord;
import com.attendance.service.AttendanceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkIn")
    public Result<Map<String, Object>> checkIn(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String photo = (String) params.get("photo");
            Float liveness = Float.valueOf(params.get("liveness").toString());
            String location = (String) params.get("location");
            String deviceInfo = (String) params.get("deviceInfo");
            
            Map<String, Object> result = attendanceService.checkIn(userId, photo, liveness, location, deviceInfo);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkOut")
    public Result<Map<String, Object>> checkOut(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String photo = (String) params.get("photo");
            Float liveness = Float.valueOf(params.get("liveness").toString());
            String location = (String) params.get("location");
            String deviceInfo = (String) params.get("deviceInfo");
            
            Map<String, Object> result = attendanceService.checkOut(userId, photo, liveness, location, deviceInfo);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/today/{userId}")
    public Result<AttendanceRecord> getTodayAttendance(@PathVariable Long userId) {
        AttendanceRecord record = attendanceService.findByUserIdAndDate(userId, LocalDate.now());
        return Result.success(record);
    }

    @GetMapping("/page")
    public Result<IPage<AttendanceRecord>> getPageRecords(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long userId) {
        IPage<AttendanceRecord> page = attendanceService.getPageRecords(current, size, startDate, endDate, userId);
        return Result.success(page);
    }

    @GetMapping("/list/{userId}")
    public Result<List<AttendanceRecord>> getUserRecords(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<AttendanceRecord> records = attendanceService.getUserRecords(userId, startDate, endDate);
        return Result.success(records);
    }

    @GetMapping("/statistics/{userId}")
    public Result<Map<String, Object>> getStatistics(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = attendanceService.getAttendanceStatistics(userId, startDate, endDate);
        return Result.success(stats);
    }
}
