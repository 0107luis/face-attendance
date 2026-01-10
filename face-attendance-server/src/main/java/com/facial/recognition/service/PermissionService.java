package com.facial.recognition.service;

import com.facial.recognition.pojo.RoleFunctionPermission;
import java.util.List;

public interface PermissionService {
    // 获取角色的所有权限
    List<RoleFunctionPermission> getRolePermissions(Integer roleId);
    
    // 为角色分配权限
    RoleFunctionPermission assignPermission(Integer roleId, Integer functionId);
    
    // 撤销角色的权限
    void revokePermission(Integer roleId, Integer functionId);
    
    // 批量分配权限
    void assignPermissions(Integer roleId, List<Integer> functionIds);
    
    // 批量撤销权限
    void revokePermissions(Integer roleId, List<Integer> functionIds);
    
    // 检查角色是否有某个权限
    boolean hasPermission(Integer roleId, Integer functionId);
}

