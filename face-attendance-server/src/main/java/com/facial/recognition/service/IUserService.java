package com.facial.recognition.service;

import com.facial.recognition.dto.LoginResponseDTO;
import com.facial.recognition.dto.RegisterDTO;
import com.facial.recognition.pojo.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    // 插入用户
    User add(User user);
    
    // 获取所有用户
    List<User> findAll();
    
    // 根据ID获取用户
    Optional<User> findById(Integer id);
    
    // 根据用户名获取用户
    Optional<User> findByUsername(String username);
    
    // 根据角色ID获取用户列表
    List<User> findByRoleId(Integer roleId);
    org.springframework.data.domain.Page<User> findByRoleIdPaged(Integer roleId, org.springframework.data.domain.Pageable pageable);
    
    // 保存用户（新增或更新）
    User save(User user);

    // 分页获取用户
    org.springframework.data.domain.Page<User> findAllPaged(org.springframework.data.domain.Pageable pageable);
    
    // 删除用户
    void delete(Integer id);
    
    // 注册用户
    LoginResponseDTO register(RegisterDTO registerDTO);
    
    // 获取登录响应信息
    LoginResponseDTO getLoginResponse(User user);
    
    // 生成学号
    String generateStudentNumber();
    
    // 生成工号
    String generateJobNumber();
}
