package com.attendance.task;

import com.attendance.entity.AttendanceRecord;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class AttendanceScheduledTask {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Scheduled(cron = "0 0 22 * * ?")
    public void autoCheckOutTask() {
        log.info("开始执行自动签退任务...");
        
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> records = attendanceRecordMapper.findByUserIdAndDateRange(
            null, today, today);
        
        for (AttendanceRecord record : records) {
            if (record.getCheckInTime() != null && record.getCheckOutTime() == null) {
                try {
                    attendanceService.checkOut(
                        record.getUserId(),
                        "",
                        0f,
                        "自动签退",
                        "系统任务"
                    );
                    log.info("用户 {} 自动签退成功", record.getUserId());
                } catch (Exception e) {
                    log.error("用户 {} 自动签退失败: {}", record.getUserId(), e.getMessage());
                }
            }
        }
        
        log.info("自动签退任务完成");
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void attendanceReminderTask() {
        log.info("发送考勤提醒...");
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyStatisticsTask() {
        log.info("生成每日考勤统计...");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<AttendanceRecord> records = attendanceRecordMapper.findByUserIdAndDateRange(
            null, yesterday, yesterday);
        
        log.info("昨日考勤记录数: {}", records.size());
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void monthlyStatisticsTask() {
        log.info("生成每月考勤统计...");
        
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = LocalDate.now().minusDays(1);
        
        log.info("{} 至 {} 月度考勤统计", firstDayOfMonth, lastDayOfMonth);
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void cleanExpiredCacheTask() {
        log.info("清理过期缓存...");
    }
}
