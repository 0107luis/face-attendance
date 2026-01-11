package com.attendance.service.impl;
import com.attendance.service.UserService;
import com.attendance.entity.AttendanceRecord;
import com.attendance.entity.User;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.AttendanceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> implements AttendanceService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Map<String, Object> checkIn(Long userId, String photo, Float liveness, String location, String deviceInfo) {
        User user = userService.getById(userId);
        LocalDate today = LocalDate.now();
        
        AttendanceRecord record = findByUserIdAndDate(userId, today);
        if (record != null && record.getCheckInTime() != null) {
            throw new RuntimeException("今日已签到");
        }
        
        if (record == null) {
            record = new AttendanceRecord();
            record.setUserId(userId);
            record.setUserName(user.getRealName());
            record.setEmployeeNo(user.getEmployeeNo());
            record.setDepartment(user.getDepartment());
            record.setAttendanceDate(today);
        }
        
        record.setCheckInTime(LocalDateTime.now());
        record.setCheckInPhoto(photo);
        record.setCheckInLiveness(liveness);
        record.setLocation(location);
        record.setDeviceInfo(deviceInfo);
        record.setStatus("normal");
        
        saveOrUpdate(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("recordId", record.getId());
        result.put("checkInTime", record.getCheckInTime());
        result.put("message", "签到成功");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> checkOut(Long userId, String photo, Float liveness, String location, String deviceInfo) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = findByUserIdAndDate(userId, today);
        
        if (record == null || record.getCheckInTime() == null) {
            throw new RuntimeException("今日尚未签到");
        }
        
        if (record.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退");
        }
        
        LocalDateTime checkOutTime = LocalDateTime.now();
        record.setCheckOutTime(checkOutTime);
        record.setCheckOutPhoto(photo);
        record.setCheckOutLiveness(liveness);
        
        Duration duration = Duration.between(record.getCheckInTime(), checkOutTime);
        int workMinutes = (int) duration.toMinutes();
        record.setWorkDuration(workMinutes);
        
        if (workMinutes < 480) {
            record.setStatus("abnormal");
        }
        
        saveOrUpdate(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("recordId", record.getId());
        result.put("checkOutTime", record.getCheckOutTime());
        result.put("workDuration", workMinutes);
        result.put("message", "签退成功");
        return result;
    }

    @Override
    public AttendanceRecord findByUserIdAndDate(Long userId, LocalDate date) {
        return baseMapper.findByUserIdAndDate(userId, date);
    }

    @Override
    public IPage<AttendanceRecord> getPageRecords(int current, int size, LocalDate startDate, LocalDate endDate, Long userId) {
        Page<AttendanceRecord> page = new Page<>(current, size);
        return baseMapper.findByDateRange(page, startDate, endDate);
    }

    @Override
    public List<AttendanceRecord> getUserRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getUserRecords(userId, startDate, endDate);
        
        int totalDays = records.size();
        int normalDays = 0;
        int abnormalDays = 0;
        int totalWorkMinutes = 0;
        
        for (AttendanceRecord record : records) {
            if ("normal".equals(record.getStatus())) {
                normalDays++;
            } else {
                abnormalDays++;
            }
            if (record.getWorkDuration() != null) {
                totalWorkMinutes += record.getWorkDuration();
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", totalDays);
        stats.put("normalDays", normalDays);
        stats.put("abnormalDays", abnormalDays);
        stats.put("totalWorkMinutes", totalWorkMinutes);
        stats.put("averageWorkMinutes", totalDays > 0 ? totalWorkMinutes / totalDays : 0);
        stats.put("records", records);
        
        return stats;
    }
}
