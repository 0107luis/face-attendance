package com.facial.recognition.repository;

import com.facial.recognition.pojo.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionEntityRepository extends JpaRepository<FunctionEntity, Integer> {
    
    // 鏍规嵁鍔熻兘鍚嶇О鏌ユ壘鍔熻兘
    List<FunctionEntity> findByFunctionName(String functionName);
    
    // 鏍规嵁鍔熻兘璺緞鏌ユ壘鍔熻兘
    Optional<FunctionEntity> findByFunctionPath(String functionPath);
    
    // 鏍规嵁鎵€灞炲瓙绯荤粺鏌ユ壘鍔熻兘
    List<FunctionEntity> findBySubsystem(String subsystem);
    
    // 鏍规嵁鐘舵€佹煡鎵惧姛锟?
    List<FunctionEntity> findByStatus(String status);
    
    // 鏍规嵁鍔熻兘鍚嶇О妯＄硦鏌ヨ
    List<FunctionEntity> findByFunctionNameContaining(String functionName);
    
    // 鏍规嵁鍔熻兘璺緞妯＄硦鏌ヨ
    List<FunctionEntity> findByFunctionPathContaining(String functionPath);
    
    // 鏍规嵁鎵€灞炲瓙绯荤粺鍜岀姸鎬佹煡鎵惧姛锟?
    List<FunctionEntity> findBySubsystemAndStatus(String subsystem, String status);
    
    // 鏍规嵁鍔熻兘鍚嶇О鍜屾墍灞炲瓙绯荤粺鏌ユ壘鍔熻兘
    List<FunctionEntity> findByFunctionNameAndSubsystem(String functionName, String subsystem);
    
    // 鑷畾涔夋煡璇細鏌ユ壘鐢ㄦ埛鏈夋潈闄愮殑鍔熻兘
    @Query("SELECT DISTINCT fe FROM FunctionEntity fe, RoleFunctionPermission rfp, Role r, User u " +
           "WHERE fe.functionId = rfp.functionId " +
           "AND rfp.roleId = r.roleId " +
           "AND r.roleId = u.RoleID " +
           "AND u.UserID = :userId AND fe.status = 'ACTIVE'")
    List<FunctionEntity> findFunctionsByUserId(@Param("userId") Integer userId);
    
    // 鑷畾涔夋煡璇細鏌ユ壘瑙掕壊鐨勫姛鑳芥潈锟?
    @Query("SELECT fe FROM FunctionEntity fe, RoleFunctionPermission rfp " +
           "WHERE fe.functionId = rfp.functionId AND rfp.roleId = :roleId AND fe.status = 'ACTIVE'")
    List<FunctionEntity> findFunctionsByRoleId(@Param("roleId") Integer roleId);
    
    // 鑷畾涔夋煡璇細鏍规嵁瀛愮郴缁熺粺璁″姛鑳芥暟锟?
    @Query("SELECT COUNT(fe) FROM FunctionEntity fe WHERE fe.subsystem = :subsystem AND fe.status = 'ACTIVE'")
    Long countBySubsystem(@Param("subsystem") String subsystem);
}
