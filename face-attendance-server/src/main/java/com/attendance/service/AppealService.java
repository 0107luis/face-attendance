package com.attendance.service;

import com.attendance.entity.AttendanceAppeal;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AppealService extends IService<AttendanceAppeal> {
    
    Map<String, Object> submitAppeal(Long recordId, Long userId, String appealType, String reason, String attachment);
    
    IPage<AttendanceAppeal> getAppeals(int current, int size, Long userId, Integer status);
    
    Map<String, Object> approveAppeal(Long appealId, String reviewer, String comment);
    
    Map<String, Object> rejectAppeal(Long appealId, String reviewer, String comment);
    
    Map<String, Object> cancelAppeal(Long appealId);
    
    List<AttendanceAppeal> getPendingAppeals(Long userId);
}
