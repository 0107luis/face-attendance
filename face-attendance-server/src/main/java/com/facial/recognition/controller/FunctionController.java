package com.facial.recognition.controller;

import com.facial.recognition.pojo.Function;
import com.facial.recognition.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/functions")
@CrossOrigin(origins = "*")
public class FunctionController {
    
    @Autowired
    private FunctionService functionService;
    
    // 获取所有功能
    @GetMapping
    public ResponseEntity<List<Function>> getAllFunctions() {
        List<Function> functions = functionService.findAll();
        return ResponseEntity.ok(functions);
    }
    
    // 获取所有激活的功能
    @GetMapping("/active")
    public ResponseEntity<List<Function>> getActiveFunctions() {
        List<Function> functions = functionService.findActiveFunctions();
        return ResponseEntity.ok(functions);
    }
    
    // 根据ID获取功能
    @GetMapping("/{id}")
    public ResponseEntity<Function> getFunctionById(@PathVariable Integer id) {
        Optional<Function> function = functionService.findById(id);
        return function.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据功能代码获取功能
    @GetMapping("/code/{functionCode}")
    public ResponseEntity<Function> getFunctionByCode(@PathVariable String functionCode) {
        Optional<Function> function = functionService.findByFunctionCode(functionCode);
        return function.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // 根据角色ID获取该角色的所有功能权限
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Function>> getFunctionsByRoleId(@PathVariable Integer roleId) {
        List<Function> functions = functionService.findFunctionsByRoleId(roleId);
        return ResponseEntity.ok(functions);
    }
}

