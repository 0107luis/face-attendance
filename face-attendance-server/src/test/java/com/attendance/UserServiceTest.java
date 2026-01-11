package com.attendance;

import com.attendance.entity.User;
import com.attendance.mapper.UserMapper;
import com.attendance.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser" + System.currentTimeMillis());
        user.setPassword("test123456");
        user.setRealName("测试用户");
        user.setEmployeeNo("EMP" + System.currentTimeMillis());
        user.setDepartment("技术部");
        
        boolean result = userService.register(user);
        assertTrue(result);
        
        User savedUser = userService.findByUsername(user.getUsername());
        assertNotNull(savedUser);
        assertEquals(user.getRealName(), savedUser.getRealName());
    }

    @Test
    public void testFindByUsername() {
        User user = userService.findByUsername("admin");
        if (user != null) {
            assertNotNull(user.getUsername());
        }
    }

    @Test
    public void testPasswordEncoder() {
        String rawPassword = "password123";
        String encoded = passwordEncoder.encode(rawPassword);
        
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
        assertFalse(passwordEncoder.matches("wrongpassword", encoded));
    }
}
