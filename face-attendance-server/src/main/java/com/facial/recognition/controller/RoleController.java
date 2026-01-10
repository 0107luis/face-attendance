package com.facial.recognition.controller;

import com.facial.recognition.pojo.Role;
import com.facial.recognition.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 创建角色
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(createdRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据ID获取角色
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // 根据角色名称获取角色
    @GetMapping("/name/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        Optional<Role> role = roleService.findByRoleName(roleName);
        return role.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // 获取所有角色
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    // 分页获取角色
    @GetMapping("/paged")
    public ResponseEntity<?> getRolesPaged(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "分页参数不合法");
            return ResponseEntity.badRequest().body(error);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> paged = roleService.findAllPaged(pageable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("content", paged.getContent());
        resp.put("totalElements", paged.getTotalElements());
        resp.put("totalPages", paged.getTotalPages());
        resp.put("page", paged.getNumber());
        resp.put("size", paged.getSize());
        return ResponseEntity.ok(resp);
    }

    // 移除依赖 status 的接口
    // @GetMapping("/status/{status}")
    // @GetMapping("/active")
    // @PatchMapping("/{id}/status")
    // @GetMapping("/count/active")

    // 根据角色名称搜索
    @GetMapping("/search/name")
    public ResponseEntity<List<Role>> searchRolesByName(@RequestParam String roleName) {
        List<Role> roles = roleService.findByRoleNameContaining(roleName);
        return ResponseEntity.ok(roles);
    }

    // 根据描述搜索
    @GetMapping("/search/description")
    public ResponseEntity<List<Role>> searchRolesByDescription(@RequestParam String description) {
        List<Role> roles = roleService.findByDescriptionContaining(description);
        return ResponseEntity.ok(roles);
    }

    // 更新角色信息
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 删除角色
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据用户ID获取角色
    @GetMapping("/user/{userId}")
    public ResponseEntity<Role> getRoleByUserId(@PathVariable Integer userId) {
        Optional<Role> role = roleService.findRoleByUserId(userId);
        return role.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // 检查角色名称是否存在
    @GetMapping("/exists/{roleName}")
    public ResponseEntity<Boolean> checkRoleNameExists(@PathVariable String roleName) {
        boolean exists = roleService.existsByRoleName(roleName);
        return ResponseEntity.ok(exists);
    }
}
