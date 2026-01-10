package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.RoleFunctionPermission;
import com.facial.recognition.repository.RoleFunctionPermissionRepository;
import com.facial.recognition.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private RoleFunctionPermissionRepository roleFunctionPermissionRepository;
    
    @Override
    public List<RoleFunctionPermission> getRolePermissions(Integer roleId) {
        return roleFunctionPermissionRepository.findByRoleId(roleId);
    }
    
    @Override
    @Transactional
    public RoleFunctionPermission assignPermission(Integer roleId, Integer functionId) {
        // 检查是否已存在
        var existing = roleFunctionPermissionRepository.findByRoleIdAndFunctionId(roleId, functionId);
        
        if (existing.isPresent()) {
            // 如果已存在，直接返回
            return existing.get();
        } else {
            // 创建新的权限分配
            RoleFunctionPermission permission = new RoleFunctionPermission(roleId, functionId);
            return roleFunctionPermissionRepository.save(permission);
        }
    }
    
    @Override
    @Transactional
    public void revokePermission(Integer roleId, Integer functionId) {
        var existing = roleFunctionPermissionRepository.findByRoleIdAndFunctionId(roleId, functionId);
        if (existing.isPresent()) {
            // 直接删除权限记录
            roleFunctionPermissionRepository.delete(existing.get());
        }
    }
    
    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> functionIds) {
        for (Integer functionId : functionIds) {
            assignPermission(roleId, functionId);
        }
    }
    
    @Override
    @Transactional
    public void revokePermissions(Integer roleId, List<Integer> functionIds) {
        for (Integer functionId : functionIds) {
            revokePermission(roleId, functionId);
        }
    }
    
    @Override
    public boolean hasPermission(Integer roleId, Integer functionId) {
        // 如果存在记录，就表示有权限
        var permission = roleFunctionPermissionRepository.findByRoleIdAndFunctionId(roleId, functionId);
        return permission.isPresent();
    }
}

