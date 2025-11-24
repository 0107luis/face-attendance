package com.attendance.service.impl;

import com.attendance.entity.AttendanceAppeal;
import com.attendance.entity.AttendanceRecord;
import com.attendance.mapper.AttendanceAppealMapper;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.AppealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppealServiceImpl extends ServiceImpl<AttendanceAppealMapper, AttendanceAppeal> implements AppealService {

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Override
    @Transactional
    public Map<String, Object> submitAppeal(Long recordId, Long userId, String appealType, String reason, String attachment) {
        AttendanceAppeal appeal = new AttendanceAppeal();
        appeal.setRecordId(recordId);
        appeal.setUserId(userId);
        appeal.setAppealType(appealType);
        appeal.setReason(reason);
        appeal.setAttachment(attachment);
        appeal.setStatus(AttendanceAppeal.STATUS_PENDING);
        
        save(appeal);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("appealId", appeal.getId());
        result.put("message", "申诉已提交");
        
        return result;
    }

    @Override
    public IPage<AttendanceAppeal> getAppeals(int current, int size, Long userId, Integer status) {
        Page<AttendanceAppeal> page = new Page<>(current, size);
        LambdaQueryWrapper<AttendanceAppeal> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(AttendanceAppeal::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(AttendanceAppeal::getStatus, status);
        }
        wrapper.orderByDesc(AttendanceAppeal::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public Map<String, Object> approveAppeal(Long appealId, String reviewer, String comment) {
        AttendanceAppeal appeal = getById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉不存在");
        }
        if (appeal.getStatus() != AttendanceAppeal.STATUS_PENDING) {
            throw new RuntimeException("申诉状态已处理");
        }
        
        // 更新申诉状态
        appeal.setStatus(AttendanceAppeal.STATUS_APPROVED);
        appeal.setReviewer(reviewer);
        appeal.setReviewComment(comment);
        appeal.setReviewTime(LocalDateTime.now());
        updateById(appeal);
        
        // 同步更新考勤记录状态
        AttendanceRecord record = attendanceRecordMapper.selectById(appeal.getRecordId());
        if (record != null) {
            // 根据申诉类型更新考勤状态
            if ("late".equals(appeal.getAppealType())) {
                record.setStatus("normal");  // 迟到申诉通过，变正常
            } else if ("absent".equals(appeal.getAppealType())) {
                record.setStatus("normal");  // 缺勤申诉通过，变正常
            } else if ("early".equals(appeal.getAppealType())) {
                record.setStatus("normal");  // 早退申诉通过，变正常
            }
            attendanceRecordMapper.updateById(record);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "申诉已通过并同步更新考勤记录");
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> rejectAppeal(Long appealId, String reviewer, String comment) {
        AttendanceAppeal appeal = getById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉不存在");
        }
        if (appeal.getStatus() != AttendanceAppeal.STATUS_PENDING) {
            throw new RuntimeException("申诉状态已处理");
        }
        
        appeal.setStatus(AttendanceAppeal.STATUS_REJECTED);
        appeal.setReviewer(reviewer);
        appeal.setReviewComment(comment);
        appeal.setReviewTime(LocalDateTime.now());
        updateById(appeal);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "申诉已拒绝");
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelAppeal(Long appealId) {
        AttendanceAppeal appeal = getById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉不存在");
        }
        if (appeal.getStatus() != AttendanceAppeal.STATUS_PENDING) {
            throw new RuntimeException("只能取消待审核的申诉");
        }
        
        removeById(appealId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "申诉已取消");
        
        return result;
    }

    @Override
    public List<AttendanceAppeal> getPendingAppeals(Long userId) {
        LambdaQueryWrapper<AttendanceAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceAppeal::getUserId, userId)
               .eq(AttendanceAppeal::getStatus, AttendanceAppeal.STATUS_PENDING)
               .orderByDesc(AttendanceAppeal::getCreateTime);
        return list(wrapper);
    }
}
