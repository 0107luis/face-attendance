package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.dto.LoginDTO;
import com.attendance.dto.RegisterDTO;
import com.attendance.entity.User;
import com.attendance.service.UserService;
import com.attendance.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.findByUsername(loginDTO.getUsername());
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return Result.error("用户名或密码错误");
            }
            
            String token = jwtUtil.generateToken(loginDTO.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("department", user.getDepartment());
            
            return Result.success(data, "登录成功");
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        try {
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(registerDTO.getPassword());
            user.setRealName(registerDTO.getRealName());
            user.setPhone(registerDTO.getPhone());
            user.setEmail(registerDTO.getEmail());
            user.setDepartment(registerDTO.getDepartment());
            user.setEmployeeNo(registerDTO.getEmployeeNo());
            
            userService.register(user);
            return Result.success(null, "注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = jwtUtil.parseToken(token);
            String username = claims.getSubject();
            User user = userService.findByUsername(username);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("phone", user.getPhone());
            data.put("email", user.getEmail());
            data.put("department", user.getDepartment());
            data.put("employeeNo", user.getEmployeeNo());
            
            return Result.success(data);
        }
        return Result.error("未登录");
    }
}
