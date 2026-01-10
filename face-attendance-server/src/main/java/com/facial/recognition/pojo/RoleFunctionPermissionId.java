package com.facial.recognition.pojo;

import java.io.Serializable;
import java.util.Objects;

/**
 * 角色功能权限复合主键类
 */
public class RoleFunctionPermissionId implements Serializable {
    private Integer roleId;
    private Integer functionId;

    public RoleFunctionPermissionId() {}

    public RoleFunctionPermissionId(Integer roleId, Integer functionId) {
        this.roleId = roleId;
        this.functionId = functionId;
    }

    public Integer getRoleId() { 
        return roleId; 
    }
    
    public void setRoleId(Integer roleId) { 
        this.roleId = roleId; 
    }

    public Integer getFunctionId() { 
        return functionId; 
    }
    
    public void setFunctionId(Integer functionId) { 
        this.functionId = functionId; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleFunctionPermissionId that = (RoleFunctionPermissionId) o;
        return Objects.equals(roleId, that.roleId) && 
               Objects.equals(functionId, that.functionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, functionId);
    }
}

