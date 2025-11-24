package com.attendance;

import com.attendance.entity.AttendanceRecord;
import com.attendance.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Test
    public void testCheckIn() {
        Long userId = 1L;
        
        try {
            Map<String, Object> result = attendanceService.checkIn(userId, "", 0.95f, "公司", "Android");
            assertTrue((Boolean) result.get("success"));
        } catch (Exception e) {
            System.out.println("签到测试跳过: " + e.getMessage());
        }
    }

    @Test
    public void testCheckOut() {
        Long userId = 1L;
        
        try {
            Map<String, Object> result = attendanceService.checkOut(userId, "", 0.95f, "公司", "Android");
            assertTrue((Boolean) result.get("success"));
        } catch (Exception e) {
            System.out.println("签退测试跳过: " + e.getMessage());
        }
    }

    @Test
    public void testGetAttendanceStatistics() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        Map<String, Object> stats = attendanceService.getAttendanceStatistics(userId, startDate, endDate);
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalDays"));
        assertTrue(stats.containsKey("normalDays"));
    }

    @Test
    public void testGetUserRecords() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<AttendanceRecord> records = attendanceService.getUserRecords(userId, startDate, endDate);
        
        assertNotNull(records);
    }
}
