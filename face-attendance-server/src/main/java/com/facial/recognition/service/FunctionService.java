package com.facial.recognition.service;

import com.facial.recognition.pojo.Function;
import java.util.List;
import java.util.Optional;

public interface FunctionService {
    // 获取所有功能
    List<Function> findAll();
    
    // 根据ID获取功能
    Optional<Function> findById(Integer functionId);
    
    // 根据功能代码获取功能
    Optional<Function> findByFunctionCode(String functionCode);
    
    // 根据模块名称获取功能
    List<Function> findByModuleName(String moduleName);
    
    // 获取所有激活的功能
    List<Function> findActiveFunctions();
    
    // 根据角色ID获取该角色的所有功能权限
    List<Function> findFunctionsByRoleId(Integer roleId);
}

