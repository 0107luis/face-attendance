package com.attendance.controller;

import com.attendance.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/dept")
public class DepartmentController {

    private final Map<Long, Map<String, Object>> deptCache = new HashMap<>();

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> depts = Arrays.asList(
            createDept(1L, "总公司", "HQ", 0L),
            createDept(2L, "技术部", "TECH", 1L),
            createDept(3L, "研发部", "RD", 2L),
            createDept(4L, "销售部", "SALES", 1L),
            createDept(5L, "人力资源部", "HR", 1L)
        );
        return Result.success(depts);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> dept = createDept(id, "部门" + id, "DEPT" + id, 1L);
        return Result.success(dept);
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

    private Map<String, Object> createDept(Long id, String name, String code, Long parentId) {
        Map<String, Object> dept = new HashMap<>();
        dept.put("id", id);
        dept.put("deptName", name);
        dept.put("deptCode", code);
        dept.put("parentId", parentId);
        dept.put("status", 1);
        return dept;
    }
}
