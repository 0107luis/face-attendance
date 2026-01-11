package com.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance_rule")
public class AttendanceRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String workStartTime;
    private String workEndTime;
    private String lateThreshold;
    private String earlyLeaveThreshold;
    private Integer requiredWorkHours;
    private Integer enableGeolocation;
    private String allowedDistance;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
