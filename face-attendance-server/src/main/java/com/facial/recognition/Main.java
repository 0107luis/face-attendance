package com.facial.recognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 人脸识别考勤系统 - 主程序入口
 * 
 * @author Your Name
 * @version 1.0.0
 */
@SpringBootApplication
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("人脸识别考勤系统启动成功！");
        System.out.println("访问地址: http://localhost:8080");
    }
}

