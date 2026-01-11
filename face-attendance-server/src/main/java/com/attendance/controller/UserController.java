package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.User;
import com.attendance.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String department) {
        IPage<User> result = userService.getPageUsers(current, size, username, department);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        userService.register(user);
        return Result.success(null, "添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success(null, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null, "删除成功");
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }
}
