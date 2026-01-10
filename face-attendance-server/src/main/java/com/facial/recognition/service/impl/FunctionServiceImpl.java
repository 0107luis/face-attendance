package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.Function;
import com.facial.recognition.repository.FunctionRepository;
import com.facial.recognition.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FunctionServiceImpl implements FunctionService {
    
    @Autowired
    private FunctionRepository functionRepository;
    
    @Override
    public List<Function> findAll() {
        return functionRepository.findAll();
    }
    
    @Override
    public Optional<Function> findById(Integer functionId) {
        return functionRepository.findById(functionId);
    }
    
    @Override
    public Optional<Function> findByFunctionCode(String functionCode) {
        return functionRepository.findByFunctionCode(functionCode);
    }
    
    @Override
    public List<Function> findByModuleName(String moduleName) {
        // 根据模块名称查询，需要添加自定义查询方法
        // 暂时返回所有功能，后续可以在 Repository 中添加查询方法
        return functionRepository.findAll();
    }
    
    @Override
    public List<Function> findActiveFunctions() {
        // 注意：数据库中使用 IsActive (Boolean)，需要调整查询
        // 暂时返回所有功能，后续需要添加查询方法
        return functionRepository.findAll().stream()
                .filter(f -> f.getIsActive() != null && f.getIsActive())
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Function> findFunctionsByRoleId(Integer roleId) {
        return functionRepository.findFunctionsByRoleId(roleId);
    }
}

