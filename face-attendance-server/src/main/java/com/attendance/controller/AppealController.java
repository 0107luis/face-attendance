package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.AttendanceAppeal;
import com.attendance.service.AppealService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appeal")
public class AppealController {

    @Autowired
    private AppealService appealService;

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAppeal(@RequestBody Map<String, Object> params) {
        try {
            Long recordId = Long.valueOf(params.get("recordId").toString());
            Long userId = Long.valueOf(params.get("userId").toString());
            String appealType = (String) params.get("appealType");
            String reason = (String) params.get("reason");
            String attachment = (String) params.get("attachment");
            
            Map<String, Object> result = appealService.submitAppeal(recordId, userId, appealType, reason, attachment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    public Result<IPage<AttendanceAppeal>> getAppeals(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        IPage<AttendanceAppeal> page = appealService.getAppeals(current, size, userId, status);
        return Result.success(page);
    }

    @PostMapping("/approve/{appealId}")
    public Result<Map<String, Object>> approveAppeal(
            @PathVariable Long appealId,
            @RequestBody Map<String, String> params) {
        try {
            String reviewer = params.get("reviewer");
            String comment = params.get("comment");
            Map<String, Object> result = appealService.approveAppeal(appealId, reviewer, comment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reject/{appealId}")
    public Result<Map<String, Object>> rejectAppeal(
            @PathVariable Long appealId,
            @RequestBody Map<String, String> params) {
        try {
            String reviewer = params.get("reviewer");
            String comment = params.get("comment");
            Map<String, Object> result = appealService.rejectAppeal(appealId, reviewer, comment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{appealId}")
    public Result<Map<String, Object>> cancelAppeal(@PathVariable Long appealId) {
        try {
            Map<String, Object> result = appealService.cancelAppeal(appealId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/pending/{userId}")
    public Result<List<AttendanceAppeal>> getPendingAppeals(@PathVariable Long userId) {
        List<AttendanceAppeal> appeals = appealService.getPendingAppeals(userId);
        return Result.success(appeals);
    }
}
