package com.attendance.mq;

import com.attendance.config.RabbitMQConfig;
import com.attendance.dto.AttendanceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AttendanceMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCheckInMessage(AttendanceMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ATTENDANCE_EXCHANGE,
                RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
                message
            );
            log.info("发送考勤消息成功, messageId: {}, userId: {}", message.getMessageId(), message.getUserId());
        } catch (Exception e) {
            log.error("发送考勤消息失败, messageId: {}, error: {}", message.getMessageId(), e.getMessage());
            throw new RuntimeException("消息发送失败", e);
        }
    }

    public void sendCheckInMessageWithRetry(AttendanceMessage message, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                sendCheckInMessage(message);
                return;
            } catch (Exception e) {
                retryCount++;
                message.setRetryCount(retryCount);
                log.warn("消息发送失败, 重试第 {} 次, messageId: {}", retryCount, message.getMessageId());
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("消息发送失败，已达最大重试次数");
                }
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断");
                }
            }
        }
    }
}
