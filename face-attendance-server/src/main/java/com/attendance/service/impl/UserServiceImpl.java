package com.attendance.service.impl;

import com.attendance.entity.User;
import com.attendance.mapper.UserMapper;
import com.attendance.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public User findByEmployeeNo(String employeeNo) {
        return baseMapper.findByEmployeeNo(employeeNo);
    }

    @Override
    public IPage<User> getPageUsers(int current, int size, String username, String department) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(department)) {
            wrapper.eq(User::getDepartment, department);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public boolean register(User user) {
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (findByEmployeeNo(user.getEmployeeNo()) != null) {
            throw new RuntimeException("工号已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        return save(user);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        return removeById(id);
    }
}
