package com.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("face_feature")
public class FaceFeature {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String faceId;
    private byte[] featureData;
    private String featureJson;
    private Integer faceCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
