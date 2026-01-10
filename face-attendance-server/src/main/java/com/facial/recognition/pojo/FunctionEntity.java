package com.facial.recognition.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "function_entities")
public class FunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FunctionID")
    private Integer functionId; // 主键

    @Column(nullable = false)
    private String functionName; // 功能名称

    @Column(name = "function_path", nullable = false)
    private String functionPath; // 功能路径/代码

    @Column(name = "subsystem", nullable = false)
    private String subsystem; // 所属子系统

    private String description; // 功能描述

    @Column(name = "created_time")
    private java.time.LocalDateTime createdTime; // 创建时间

    @Column(name = "updated_time")
    private java.time.LocalDateTime updatedTime; // 更新时间

    private String status; // 状态：ACTIVE, INACTIVE

    public FunctionEntity() {}

    public FunctionEntity(String functionName, String functionPath, String subsystem) {
        this.functionName = functionName;
        this.functionPath = functionPath;
        this.subsystem = subsystem;
        this.createdTime = java.time.LocalDateTime.now();
        this.updatedTime = java.time.LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public String getFunctionPath() { return functionPath; }
    public void setFunctionPath(String functionPath) { this.functionPath = functionPath; }

    public String getSubsystem() { return subsystem; }
    public void setSubsystem(String subsystem) { this.subsystem = subsystem; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public java.time.LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(java.time.LocalDateTime createdTime) { this.createdTime = createdTime; }

    public java.time.LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(java.time.LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

