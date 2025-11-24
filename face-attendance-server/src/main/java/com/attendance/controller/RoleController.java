package com.attendance.controller;

import com.attendance.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> roles = Arrays.asList(
            createRole(1L, "管理员", "admin", "系统管理员"),
            createRole(2L, "经理", "manager", "部门经理"),
            createRole(3L, "普通员工", "user", "普通员工")
        );
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> role = createRole(id, "角色" + id, "role_" + id, "描述");
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        return Result.success(null, "添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        return Result.success(null, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return Result.success(null, "删除成功");
    }

    private Map<String, Object> createRole(Long id, String name, String key, String desc) {
        Map<String, Object> role = new HashMap<>();
        role.put("id", id);
        role.put("roleName", name);
        role.put("roleKey", key);
        role.put("description", desc);
        role.put("status", 1);
        return role;
    }
}
