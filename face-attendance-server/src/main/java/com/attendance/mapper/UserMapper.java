package com.attendance.mapper;

import com.attendance.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM sys_user WHERE employee_no = #{employeeNo} AND deleted = 0")
    User findByEmployeeNo(@Param("employeeNo") String employeeNo);
}
