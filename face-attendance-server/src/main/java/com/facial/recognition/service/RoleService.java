package com.facial.recognition.service;

import com.facial.recognition.pojo.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    
    // 创建角色
    Role createRole(Role role);
    
    // 根据ID查找角色
    Optional<Role> findById(Integer roleId);
    
    // 根据角色名称查找角色
    Optional<Role> findByRoleName(String roleName);
    
    // 获取所有角�?
    List<Role> findAll();
    
    // 根据状态查找角�?
    List<Role> findByStatus(String status);
    
    // 查找活跃状态的角色
    List<Role> findActiveRoles();
    
    // 根据角色名称模糊查询
    List<Role> findByRoleNameContaining(String roleName);
    
    // 根据描述模糊查询
    List<Role> findByDescriptionContaining(String description);

    // 分页查询
    org.springframework.data.domain.Page<Role> findAllPaged(org.springframework.data.domain.Pageable pageable);
    
    // 更新角色信息
    Role updateRole(Integer roleId, Role role);
    
    // 更新角色状�?
    Role updateStatus(Integer roleId, String status);
    
    // 删除角色
    void deleteRole(Integer roleId);
    
    // 根据用户ID查找角色
    Optional<Role> findRoleByUserId(Integer userId);
    
    // 统计活跃角色数量
    Long countActiveRoles();
    
    // 检查角色名称是否存�?
    boolean existsByRoleName(String roleName);
}
