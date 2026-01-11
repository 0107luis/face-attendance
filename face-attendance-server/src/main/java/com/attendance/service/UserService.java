package com.attendance.service;

import com.attendance.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.context.annotation.Bean;


public interface UserService extends IService<User> {
    
    User findByUsername(String username);
    
    User findByEmployeeNo(String employeeNo);
    
    IPage<User> getPageUsers(int current, int size, String username, String department);
    
    boolean register(User user);
    
    boolean updateUser(User user);
    
    boolean deleteUser(Long id);
}
