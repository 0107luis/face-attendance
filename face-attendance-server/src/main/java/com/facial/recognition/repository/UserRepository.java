package com.facial.recognition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.facial.recognition.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 根据用户名查找用户
    Optional<User> findByUserName(String userName);
    
    // 获取全部用户，管理员优先，其余按创建时间倒序
    @Query("SELECT u FROM User u " +
           "ORDER BY CASE WHEN u.RoleID = 1 THEN 0 ELSE 1 END, u.createdDate DESC, u.UserID DESC")
    List<User> findAllOrdered();

    // 分页获取全部用户，管理员优先，其余按创建时间倒序
    @Query(
        value = "SELECT u FROM User u " +
                "ORDER BY CASE WHEN u.RoleID = 1 THEN 0 ELSE 1 END, u.createdDate DESC, u.UserID DESC",
        countQuery = "SELECT COUNT(u) FROM User u"
    )
    Page<User> findAllOrdered(Pageable pageable);

    // 根据角色ID查找用户列表
    @Query("SELECT u FROM User u WHERE u.RoleID = :roleId ORDER BY u.createdDate DESC, u.UserID DESC")
    List<User> findByRoleId(@Param("roleId") Integer roleId);

    // 根据角色ID分页查找用户列表
    @Query(
        value = "SELECT u FROM User u WHERE u.RoleID = :roleId ORDER BY u.createdDate DESC, u.UserID DESC",
        countQuery = "SELECT COUNT(u) FROM User u WHERE u.RoleID = :roleId"
    )
    Page<User> findByRoleId(@Param("roleId") Integer roleId, Pageable pageable);
}
