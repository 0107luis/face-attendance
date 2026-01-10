package com.facial.recognition.repository;

import com.facial.recognition.pojo.RoleFunctionPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleFunctionPermissionRepository extends JpaRepository<RoleFunctionPermission, Long> {
    
    // 閺嶈宓佺憴鎺曞ID閺屻儲澹橀弶鍐
    List<RoleFunctionPermission> findByRoleId(Integer roleId);
    
    // 閺嶈宓侀崝鐔诲厴ID閺屻儲澹橀弶鍐
    List<RoleFunctionPermission> findByFunctionId(Integer functionId);
    
    // 根据角色ID和功能ID查找权限
    Optional<RoleFunctionPermission> findByRoleIdAndFunctionId(Integer roleId, Integer functionId);
    
    // 查找用户对特定功能的权限（如果存在记录就表示有权限）
    @Query("SELECT rfp FROM RoleFunctionPermission rfp " +
           "JOIN User u ON rfp.roleId = u.RoleID " +
           "WHERE u.UserID = :userId AND rfp.functionId = :functionId")
    List<RoleFunctionPermission> findUserPermissionsForFunction(
        @Param("userId") Integer userId, @Param("functionId") Integer functionId);
    
    // 查找角色的所有权限（如果存在记录就表示已授权）
    @Query("SELECT rfp FROM RoleFunctionPermission rfp " +
           "WHERE rfp.roleId = :roleId")
    List<RoleFunctionPermission> findGrantedPermissionsByRoleId(@Param("roleId") Integer roleId);
    
    // 閼奉亜鐣炬稊澶嬬叀鐠囶澁绱伴懢宄板絿閸旂喕鍏橀惃鍕閺堝娼堥梽鎰瀻閿?
    @Query("SELECT rfp FROM RoleFunctionPermission rfp " +
           "WHERE rfp.functionId = :functionId")
    List<RoleFunctionPermission> findAllPermissionsForFunction(@Param("functionId") Integer functionId);
    
    // 閸掔娀娅庣憴鎺曞閻ㄥ嫭澧嶉張澶嬫綀閿?
    void deleteByRoleId(Integer roleId);
    
    // 閸掔娀娅庨崝鐔诲厴閻ㄥ嫭澧嶉張澶嬫綀閿?
    void deleteByFunctionId(Integer functionId);
}
