package com.attendance.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ATTENDANCE_EXCHANGE = "attendance.exchange";
    public static final String ATTENDANCE_QUEUE = "attendance.queue";
    public static final String ATTENDANCE_ROUTING_KEY = "attendance.checkin";
    
    public static final String ATTENDANCE_DLX = "attendance.dlx";
    public static final String ATTENDANCE_DLQ = "attendance.dlq";

    @Bean
    public DirectExchange attendanceExchange() {
        return new DirectExchange(ATTENDANCE_EXCHANGE);
    }

    @Bean
    public Queue attendanceQueue() {
        return QueueBuilder.durable(ATTENDANCE_QUEUE)
                .withArgument("x-dead-letter-exchange", ATTENDANCE_DLX)
                .withArgument("x-dead-letter-routing-key", ATTENDANCE_DLQ)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(ATTENDANCE_DLX);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(ATTENDANCE_DLQ).build();
    }

    @Bean
    public Binding attendanceBinding(Queue attendanceQueue, DirectExchange attendanceExchange) {
        return BindingBuilder.bind(attendanceQueue).to(attendanceExchange).with(ATTENDANCE_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ATTENDANCE_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
