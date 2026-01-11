package com.attendance;

import com.attendance.service.AntiCheatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AntiCheatServiceTest {

    @Autowired
    private AntiCheatService antiCheatService;

    @Test
    public void testCheckRisk() {
        Map<String, Object> result = antiCheatService.checkRisk(
            1L, "device123", "192.168.1.100", "公司", "checkIn");
        
        assertNotNull(result);
        assertNotNull(result.get("isRisk"));
        assertNotNull(result.get("allowCheck"));
    }

    @Test
    public void testRecordCheck() {
        boolean result = antiCheatService.recordCheck(
            1L, "device123", "192.168.1.100", "公司", "checkIn");
        
        assertTrue(result);
    }

    @Test
    public void testGetUserCheckHistory() {
        List<?> history = antiCheatService.getUserCheckHistory(1L, 7);
        assertNotNull(history);
    }

    @Test
    public void testIsDeviceBlocked() {
        boolean blocked = antiCheatService.isDeviceBlocked("blocked_device");
        assertFalse(blocked);
    }
}
