package com.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance_appeal")
public class AttendanceAppeal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long userId;
    private String appealType;
    private String reason;
    private String attachment;
    private Integer status;
    private String reviewer;
    private String reviewComment;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_APPROVED = 1;
    public static final Integer STATUS_REJECTED = 2;
    
    public static final String TYPE_MISSED_CHECKIN = "missed_checkin";
    public static final String TYPE_MISSED_CHECKOUT = "missed_checkout";
    public static final String TYPE_LATE = "late";
    public static final String TYPE_LEAVE_EARLY = "leave_early";
    public static final String TYPE_OTHER = "other";
}
