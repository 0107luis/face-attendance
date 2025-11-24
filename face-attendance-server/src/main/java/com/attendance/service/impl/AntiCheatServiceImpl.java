package com.attendance.service.impl;

import com.attendance.entity.AttendanceAntiCheat;
import com.attendance.mapper.AttendanceAntiCheatMapper;
import com.attendance.service.AntiCheatService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AntiCheatServiceImpl extends ServiceImpl<AttendanceAntiCheatMapper, AttendanceAntiCheat> implements AntiCheatService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String DEVICE_BLOCK_KEY = "anti_cheat:device:block:";
    private static final String IP_BLOCK_KEY = "anti_cheat:ip:block:";
    private static final String CHECK_COUNT_KEY = "anti_cheat:check:count:";
    private static final int MAX_CHECK_PER_HOUR = 10;
    private static final int MAX_CHECK_PER_DAY = 30;

    @Override
    public Map<String, Object> checkRisk(Long userId, String deviceId, String ipAddress, 
                                         String location, String checkType) {
        Map<String, Object> result = new HashMap<>();
        boolean isRisk = false;
        String riskReason = "";

        if (isDeviceBlocked(deviceId)) {
            isRisk = true;
            riskReason = "设备已被封禁";
        }

        if (isIpBlocked(ipAddress)) {
            isRisk = true;
            riskReason = "IP已被封禁";
        }

        Long hourCheckCount = redisTemplate.opsForValue().increment(
            CHECK_COUNT_KEY + userId + ":hour:" + LocalDateTime.now().getHour(), 1);
        if (hourCheckCount != null && hourCheckCount > MAX_CHECK_PER_HOUR) {
            isRisk = true;
            riskReason = "操作过于频繁";
        }
        redisTemplate.expire(CHECK_COUNT_KEY + userId + ":hour:" + LocalDateTime.now().getHour(), 2, TimeUnit.HOURS);

        Long dayCheckCount = redisTemplate.opsForValue().increment(
            CHECK_COUNT_KEY + userId + ":day:" + LocalDateTime.now().toLocalDate(), 1);
        if (dayCheckCount != null && dayCheckCount > MAX_CHECK_PER_DAY) {
            isRisk = true;
            riskReason = "今日签到次数超限";
        }
        redisTemplate.expire(CHECK_COUNT_KEY + userId + ":day:" + LocalDateTime.now().toLocalDate(), 25, TimeUnit.HOURS);

        result.put("isRisk", isRisk);
        result.put("riskReason", riskReason);
        result.put("allowCheck", !isRisk);

        return result;
    }

    @Override
    public boolean recordCheck(Long userId, String deviceId, String ipAddress, 
                              String location, String checkType) {
        AttendanceAntiCheat record = new AttendanceAntiCheat();
        record.setUserId(userId);
        record.setDeviceId(deviceId);
        record.setIpAddress(ipAddress);
        record.setLocation(location);
        record.setCheckType(checkType);
        record.setCheckTime(LocalDateTime.now());
        record.setStatus("normal");
        record.setRiskLevel("low");
        
        return save(record);
    }

    @Override
    public List<AttendanceAntiCheat> getUserCheckHistory(Long userId, int days) {
        LambdaQueryWrapper<AttendanceAntiCheat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceAntiCheat::getUserId, userId)
               .ge(AttendanceAntiCheat::getCheckTime, LocalDateTime.now().minusDays(days))
               .orderByDesc(AttendanceAntiCheat::getCheckTime);
        return list(wrapper);
    }

    @Override
    public boolean isDeviceBlocked(String deviceId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(DEVICE_BLOCK_KEY + deviceId));
    }

    @Override
    public boolean isIpBlocked(String ipAddress) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(IP_BLOCK_KEY + ipAddress));
    }
}
