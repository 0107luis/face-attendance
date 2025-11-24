package com.attendance.service;

import com.attendance.entity.AttendanceAntiCheat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AntiCheatService extends IService<AttendanceAntiCheat> {
    
    Map<String, Object> checkRisk(Long userId, String deviceId, String ipAddress, 
                                   String location, String checkType);
    
    boolean recordCheck(Long userId, String deviceId, String ipAddress, 
                       String location, String checkType);
    
    List<AttendanceAntiCheat> getUserCheckHistory(Long userId, int days);
    
    boolean isDeviceBlocked(String deviceId);
    
    boolean isIpBlocked(String ipAddress);
}
