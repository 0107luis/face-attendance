package com.facial.recognition.repository;

import com.facial.recognition.pojo.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Integer> {
    
    // 根据功能代码查找功能
    Optional<Function> findByFunctionCode(String functionCode);
    
    // 根据功能名称查找功能
    List<Function> findByFunctionName(String functionName);
    
    // 根据是否激活查找功能
    List<Function> findByIsActive(Boolean isActive);
    
    // 根据URL查找功能
    List<Function> findByUrl(String url);
    
    // 根据HTTP方法查找功能
    List<Function> findByMethod(String method);
    
    // 根据功能名称模糊查询
    List<Function> findByFunctionNameContaining(String functionName);
    
    // 按排序顺序查找激活的功能
    List<Function> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    // 根据模块名称查找功能
    List<Function> findByModuleName(String moduleName);
    
    // 自定义查询：查找用户有权限的功能
    @Query("SELECT DISTINCT f FROM Function f, RoleFunctionPermission rfp, Role r, User u " +
           "WHERE f.functionId = rfp.functionId " +
           "AND rfp.roleId = r.roleId " +
           "AND r.roleId = u.RoleID " +
           "AND u.UserID = :userId AND (f.isActive = true OR f.isActive IS NULL)")
    List<Function> findFunctionsByUserId(@Param("userId") Integer userId);
    
    // 自定义查询：查找角色的功能权限
    @Query("SELECT f FROM Function f, RoleFunctionPermission rfp " +
           "WHERE f.functionId = rfp.functionId AND rfp.roleId = :roleId")
    List<Function> findFunctionsByRoleId(@Param("roleId") Integer roleId);
}
