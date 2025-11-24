package com.attendance.mapper;

import com.attendance.entity.AttendanceRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {
    
    @Select("SELECT * FROM attendance_record WHERE user_id = #{userId} AND attendance_date = #{date} AND deleted = 0")
    AttendanceRecord findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Select("SELECT * FROM attendance_record WHERE attendance_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY attendance_date DESC, check_in_time DESC")
    IPage<AttendanceRecord> findByDateRange(Page<AttendanceRecord> page, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT * FROM attendance_record WHERE user_id = #{userId} AND attendance_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY attendance_date DESC")
    List<AttendanceRecord> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
