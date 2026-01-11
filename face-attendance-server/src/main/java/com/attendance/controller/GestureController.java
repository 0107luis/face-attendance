package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.AttendanceGesture;
import com.attendance.service.GestureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gesture")
public class GestureController {

    @Autowired
    private GestureService gestureService;

    @PostMapping("/save")
    public Result<Map<String, Object>> saveGesture(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String gestureType = (String) params.get("gestureType");
            String gestureData = (String) params.get("gestureData");
            
            boolean result = gestureService.saveGesture(userId, gestureType, gestureData);
            return Result.success(Map.of("success", result));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public Result<Map<String, Object>> verifyGesture(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String gestureType = (String) params.get("gestureType");
            String inputData = (String) params.get("inputData");
            
            boolean result = gestureService.verifyGesture(userId, gestureType, inputData);
            return Result.success(Map.of("verified", result));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userId}")
    public Result<Map<String, Object>> deleteGesture(@PathVariable Long userId) {
        boolean result = gestureService.deleteGesture(userId);
        return Result.success(Map.of("success", result));
    }

    @GetMapping("/list/{userId}")
    public Result<List<AttendanceGesture>> getUserGestures(@PathVariable Long userId) {
        List<AttendanceGesture> gestures = gestureService.getUserGestures(userId);
        return Result.success(gestures);
    }
}
