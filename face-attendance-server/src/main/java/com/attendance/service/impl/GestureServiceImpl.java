package com.attendance.service.impl;

import com.attendance.entity.AttendanceGesture;
import com.attendance.mapper.AttendanceGestureMapper;
import com.attendance.service.GestureService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GestureServiceImpl extends ServiceImpl<AttendanceGestureMapper, AttendanceGesture> implements GestureService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String GESTURE_VERIFY_KEY = "gesture:verify:";

    @Override
    public boolean saveGesture(Long userId, String gestureType, String gestureData) {
        LambdaQueryWrapper<AttendanceGesture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceGesture::getUserId, userId)
               .eq(AttendanceGesture::getGestureType, gestureType);
        
        AttendanceGesture existing = getOne(wrapper);
        
        if (existing != null) {
            existing.setGestureData(gestureData);
            return updateById(existing);
        } else {
            AttendanceGesture gesture = new AttendanceGesture();
            gesture.setUserId(userId);
            gesture.setGestureType(gestureType);
            gesture.setGestureData(gestureData);
            gesture.setStatus(1);
            return save(gesture);
        }
    }

    @Override
    public boolean verifyGesture(Long userId, String gestureType, String inputData) {
        String verifyKey = GESTURE_VERIFY_KEY + userId + ":" + gestureType;
        Boolean isVerified = (Boolean) redisTemplate.opsForValue().get(verifyKey);
        
        if (Boolean.TRUE.equals(isVerified)) {
            return true;
        }
        
        LambdaQueryWrapper<AttendanceGesture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceGesture::getUserId, userId)
               .eq(AttendanceGesture::getGestureType, gestureType)
               .eq(AttendanceGesture::getStatus, 1);
        
        AttendanceGesture gesture = getOne(wrapper);
        
        if (gesture == null) {
            return false;
        }
        
        boolean matched = gesture.getGestureData().equals(inputData);
        
        if (matched) {
            redisTemplate.opsForValue().set(verifyKey, true, 5 * 60);
        }
        
        return matched;
    }

    @Override
    public boolean deleteGesture(Long userId) {
        LambdaQueryWrapper<AttendanceGesture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceGesture::getUserId, userId);
        return remove(wrapper);
    }

    @Override
    public List<AttendanceGesture> getUserGestures(Long userId) {
        LambdaQueryWrapper<AttendanceGesture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceGesture::getUserId, userId);
        return list(wrapper);
    }
}
