package com.facial.recognition.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "Role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Integer roleId;

    @Column(name = "RoleName", nullable = false, unique = true)
    private String roleName; // 角色名称

    @Column(name = "RoleDescription")
    private String description; // 角色描述

    @Column(name = "CreatedDate")
    private java.time.LocalDateTime createdTime; // 创建时间

    public Role() {}

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
        this.createdTime = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public java.time.LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(java.time.LocalDateTime createdTime) { this.createdTime = createdTime; }
}
