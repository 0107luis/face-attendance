package com.facial.recognition.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "RoleFunctionPermission")
@IdClass(RoleFunctionPermissionId.class)
public class RoleFunctionPermission implements Serializable {
    
    @Id
    @Column(name = "RoleID", nullable = false)
    private Integer roleId; // 主键，外键

    @Id
    @Column(name = "FunctionID", nullable = false)
    private Integer functionId; // 主键，外键

    @Column(name = "AssignedDate")
    private LocalDateTime assignedDate; // 分配时间

    // 为了兼容现有代码，保留这些字段，但可能不在数据库表中
    @Transient
    private String permissionType; // 权限类型：READ, WRITE, DELETE, EXECUTE

    @Transient
    private Boolean granted; // 是否授权，默认为true（如果存在记录就表示已授权）

    public RoleFunctionPermission() {}

    public RoleFunctionPermission(Integer roleId, Integer functionId) {
        this.roleId = roleId;
        this.functionId = functionId;
        this.assignedDate = LocalDateTime.now();
        this.granted = true; // 如果存在记录，表示已授权
    }
    
    public RoleFunctionPermission(Integer roleId, Integer functionId, String permissionType, Boolean granted) {
        this.roleId = roleId;
        this.functionId = functionId;
        this.permissionType = permissionType;
        this.granted = granted;
        this.assignedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }

    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }

    public Boolean getGranted() { return granted; }
    public void setGranted(Boolean granted) { this.granted = granted; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
}

