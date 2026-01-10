package com.facial.recognition.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * 自定义物理命名策略，确保表名和列名保持原样（不转换为小写）
 * 用于解决MySQL表名大小写敏感的问题
 */
public class CustomPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        // 保持表名原样，不进行任何转换
        return name;
    }
    
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        // 保持列名原样，不进行任何转换
        return name;
    }
}

