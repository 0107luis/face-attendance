package com.facial.recognition.controller;

import com.facial.recognition.pojo.RoleFunctionPermission;
import com.facial.recognition.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    // 获取角色的所有权限
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<RoleFunctionPermission>> getRolePermissions(@PathVariable Integer roleId) {
        List<RoleFunctionPermission> permissions = permissionService.getRolePermissions(roleId);
        return ResponseEntity.ok(permissions);
    }
    
    // 为角色分配单个权限
    @PostMapping("/role/{roleId}/function/{functionId}")
    public ResponseEntity<RoleFunctionPermission> assignPermission(
            @PathVariable Integer roleId,
            @PathVariable Integer functionId) {
        try {
            RoleFunctionPermission permission = permissionService.assignPermission(roleId, functionId);
            return ResponseEntity.ok(permission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 撤销角色的单个权限
    @DeleteMapping("/role/{roleId}/function/{functionId}")
    public ResponseEntity<Void> revokePermission(
            @PathVariable Integer roleId,
            @PathVariable Integer functionId) {
        try {
            permissionService.revokePermission(roleId, functionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 批量分配权限
    @PostMapping("/role/{roleId}/assign")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable Integer roleId,
            @RequestBody Map<String, List<Integer>> request) {
        try {
            List<Integer> functionIds = request.get("functionIds");
            if (functionIds == null || functionIds.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            permissionService.assignPermissions(roleId, functionIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 批量撤销权限
    @PostMapping("/role/{roleId}/revoke")
    public ResponseEntity<Void> revokePermissions(
            @PathVariable Integer roleId,
            @RequestBody Map<String, List<Integer>> request) {
        try {
            List<Integer> functionIds = request.get("functionIds");
            if (functionIds == null || functionIds.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            permissionService.revokePermissions(roleId, functionIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 检查角色是否有某个权限
    @GetMapping("/role/{roleId}/function/{functionId}/check")
    public ResponseEntity<Boolean> hasPermission(
            @PathVariable Integer roleId,
            @PathVariable Integer functionId) {
        boolean hasPermission = permissionService.hasPermission(roleId, functionId);
        return ResponseEntity.ok(hasPermission);
    }
}

