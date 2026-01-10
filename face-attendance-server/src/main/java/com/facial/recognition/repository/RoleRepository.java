package com.facial.recognition.repository;

import com.facial.recognition.pojo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    // 根据角色名称查找角色
    Optional<Role> findByRoleName(String roleName);
    
    // 移除依赖 status 的查询方法，因为数据库中没有该字段
    // List<Role> findByStatus(String status);
    // List<Role> findByStatusOrderByRoleNameAsc(String status);
    
    // 根据角色名称模糊查询
    List<Role> findByRoleNameContaining(String roleName);
    
    // 根据描述模糊查询
    List<Role> findByDescriptionContaining(String description);
    
    // 根据用户ID查找角色 (User 表中有 RoleID 外键)
    // 注意：User 实体类中需正确映射 RoleID
    @Query("SELECT r FROM Role r, User u WHERE r.roleId = u.RoleID AND u.UserID = :userId")
    Optional<Role> findRoleByUserId(@Param("userId") Integer userId);
    
    // 移除依赖 status 的统计方法
    // Long countActiveRoles();
}
