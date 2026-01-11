# Face Attendance System

企业级人脸识别考勤系统，基于FaceAISDK离线版SDK开发。

## 技术栈

- **后端**: Spring Boot 3.2 + MySQL + Redis + RabbitMQ
- **移动端**: Android (Kotlin) + FaceAISDK
- **小程序**: 微信原生开发

## 功能特性

- 人脸识别签到/签退
- 活体检测防作弊
- GPS定位签到
- 手势密码签到
- 考勤数据统计
- 考勤申诉管理
- MQ消息队列
- 定时任务

## 项目结构

```
face-attendance/
├── face-attendance-server/     # Spring Boot服务端
├── face-attendance-android/    # Android客户端
├── face-attendance-miniprogram/  # 微信小程序
├── docs/                       # 设计文档
└── test/                      # 测试脚本
```

## 快速开始

### 服务端

```bash
cd face-attendance-server
mvn spring-boot:run
```

### Android客户端

使用Android Studio打开 `face-attendance-android` 目录，集成 `faceAILib` 模块后编译运行。

## API文档

详见 `docs/` 目录下的需求分析和工程报告文档。

## License

MIT
