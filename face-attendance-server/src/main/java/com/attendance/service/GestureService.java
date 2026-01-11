package com.attendance.service;

import com.attendance.entity.AttendanceGesture;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface GestureService extends IService<AttendanceGesture> {
    
    boolean saveGesture(Long userId, String gestureType, String gestureData);
    
    boolean verifyGesture(Long userId, String gestureType, String inputData);
    
    boolean deleteGesture(Long userId);
    
    List<AttendanceGesture> getUserGestures(Long userId);
}
