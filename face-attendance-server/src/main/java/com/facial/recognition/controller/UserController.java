package com.facial.recognition.controller;

import com.facial.recognition.dto.LoginDTO;
import com.facial.recognition.dto.LoginResponseDTO;
import com.facial.recognition.dto.RegisterDTO;
import com.facial.recognition.pojo.User;
import com.facial.recognition.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private IUserService userService;
    
    // 获取所有用户
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // 分页获取用户
    @GetMapping("/paged")
    public ResponseEntity<?> getUsersPaged(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "分页参数不合法");
            return ResponseEntity.badRequest().body(error);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> paged = userService.findAllPaged(pageable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", paged.getContent());
        resp.put("totalElements", paged.getTotalElements());
        resp.put("totalPages", paged.getTotalPages());
        resp.put("page", paged.getNumber());
        resp.put("size", paged.getSize());
        return ResponseEntity.ok(resp);
    }
    
    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据用户名获取用户
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据角色ID获取用户列表
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable Integer roleId) {
        List<User> users = userService.findByRoleId(roleId);
        return ResponseEntity.ok(users);
    }

    // 根据角色ID分页获取用户列表
    @GetMapping("/role/{roleId}/paged")
    public ResponseEntity<?> getUsersByRoleIdPaged(@PathVariable Integer roleId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "分页参数不合法");
            return ResponseEntity.badRequest().body(error);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> paged = userService.findByRoleIdPaged(roleId, pageable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", paged.getContent());
        resp.put("totalElements", paged.getTotalElements());
        resp.put("totalPages", paged.getTotalPages());
        resp.put("page", paged.getNumber());
        resp.put("size", paged.getSize());
        return ResponseEntity.ok(resp);
    }

    // 登录接口 - 返回完整用户信息
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        logger.info("收到登录请求: 用户名 [{}]", loginDTO.getUsername());
        
        return userService.findByUsername(loginDTO.getUsername())
                .map(user -> {
                    logger.info("数据库找到用户: ID={}", user.getUserID());
                    if (user.getPassword().equals(loginDTO.getPassword())) {
                        logger.info("密码匹配成功！用户: {}", user.getUserName());
                        // 返回完整的登录响应信息
                        LoginResponseDTO response = userService.getLoginResponse(user);
                        return ResponseEntity.ok(response);
                    } else {
                        logger.warn("密码不匹配！尝试登录用户: {}", loginDTO.getUsername());
                        Map<String, String> error = new HashMap<>();
                        error.put("message", "密码错误");
                        return ResponseEntity.status(401).body(error);
                    }
                })
                .orElseGet(() -> {
                    logger.warn("数据库未找到用户: {}", loginDTO.getUsername());
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "用户不存在");
                    return ResponseEntity.status(404).body(error);
                });
    }
    
    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        logger.info("收到注册请求: 用户名 [{}], 角色ID [{}]", registerDTO.getUsername(), registerDTO.getRoleId());
        
        try {
            LoginResponseDTO response = userService.register(registerDTO);
            logger.info("注册成功: 用户ID [{}]", response.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("注册失败: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("注册异常: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // 创建用户
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findById(id)
                .map(existingUser -> {
                    user.setUserID(id);
                    User updatedUser = userService.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无效的用户ID");
            return ResponseEntity.badRequest().body(response);
        }
        
        return userService.findById(id)
                .map(user -> {
                    try {
                        userService.delete(id);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "用户删除成功");
                        return ResponseEntity.ok(response);
                    } catch (IllegalStateException e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", e.getMessage());
                        return ResponseEntity.status(409).body(response); // Conflict
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "删除失败: " + e.getMessage());
                        return ResponseEntity.status(500).body(response);
                    }
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "用户不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }
}

