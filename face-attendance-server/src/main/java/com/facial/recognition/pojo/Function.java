package com.facial.recognition.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "Function")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FunctionID")
    private Integer functionId;

    @Column(name = "FunctionName", nullable = false)
    private String functionName; // 功能名称

    @Column(name = "Description")
    private String description; // 功能描述

    @Column(name = "FunctionCode", unique = true)
    private String functionCode; // 功能代码
    
    @Column(name = "ModuleName")
    private String moduleName; // 模块名称

    @Column(name = "Url")
    private String url; // 功能URL

    @Column(name = "Method")
    private String method; // HTTP方法

    @Column(name = "SortOrder")
    private Integer sortOrder; // 排序

    @Column(name = "IsActive")
    private Boolean isActive; // 是否启用

    @Column(name = "CreatedDate")
    private java.time.LocalDateTime createdTime; // 创建时间

    public Function() {}

    public Function(String functionName, String functionCode) {
        this.functionName = functionName;
        this.functionCode = functionCode;
        this.createdTime = java.time.LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFunctionCode() { return functionCode; }
    public void setFunctionCode(String functionCode) { this.functionCode = functionCode; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public java.time.LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(java.time.LocalDateTime createdTime) { this.createdTime = createdTime; }
}

