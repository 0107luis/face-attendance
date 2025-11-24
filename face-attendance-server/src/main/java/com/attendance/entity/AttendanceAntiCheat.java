package com.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance_anti_cheat")
public class AttendanceAntiCheat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String deviceId;
    private String ipAddress;
    private String location;
    private String checkType;
    private LocalDateTime checkTime;
    private String status;
    private String riskLevel;
    private String riskReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
