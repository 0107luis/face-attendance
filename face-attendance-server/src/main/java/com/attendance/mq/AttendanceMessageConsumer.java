package com.attendance.mq;

import com.attendance.config.RabbitMQConfig;
import com.attendance.dto.AttendanceMessage;
import com.attendance.service.AttendanceService;
import com.attendance.service.AntiCheatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class AttendanceMessageConsumer {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AntiCheatService antiCheatService;

    @RabbitListener(queues = RabbitMQConfig.ATTENDANCE_QUEUE)
    public void handleAttendanceMessage(AttendanceMessage message) {
        log.info("收到考勤消息, messageId: {}, userId: {}, checkType: {}", 
                message.getMessageId(), message.getUserId(), message.getCheckType());

        try {
            if ("checkIn".equals(message.getCheckType())) {
                Map<String, Object> result = attendanceService.checkIn(
                    message.getUserId(),
                    message.getPhoto(),
                    message.getLiveness(),
                    message.getLocation(),
                    message.getDeviceInfo()
                );
                log.info("签到处理成功, userId: {}, result: {}", message.getUserId(), result);
            } else if ("checkOut".equals(message.getCheckType())) {
                Map<String, Object> result = attendanceService.checkOut(
                    message.getUserId(),
                    message.getPhoto(),
                    message.getLiveness(),
                    message.getLocation(),
                    message.getDeviceInfo()
                );
                log.info("签退处理成功, userId: {}, result: {}", message.getUserId(), result);
            }

            antiCheatService.recordCheck(
                message.getUserId(),
                message.getDeviceId(),
                message.getIpAddress(),
                message.getLocation(),
                message.getCheckType()
            );

        } catch (Exception e) {
            log.error("处理考勤消息失败, messageId: {}, error: {}", message.getMessageId(), e.getMessage());
            throw new RuntimeException("消息处理失败", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ATTENDANCE_DLQ)
    public void handleDeadLetterMessage(AttendanceMessage message) {
        log.error("收到死信消息, messageId: {}, userId: {}, retryCount: {}", 
                message.getMessageId(), message.getUserId(), message.getRetryCount());
        
        if (message.getRetryCount() < 3) {
            message.setRetryCount(message.getRetryCount() + 1);
            log.info("准备重试发送消息, messageId: {}, retryCount: {}", message.getMessageId(), message.getRetryCount());
        } else {
            log.error("消息处理失败已达最大重试次数, messageId: {}", message.getMessageId());
        }
    }
}
