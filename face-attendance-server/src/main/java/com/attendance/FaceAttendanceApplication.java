package com.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.attendance.mapper")
@EnableTransactionManagement
@EnableScheduling
public class FaceAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceAttendanceApplication.class, args);
    }
}
