package com.attendance.mapper;

import com.attendance.entity.FaceFeature;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FaceFeatureMapper extends BaseMapper<FaceFeature> {
    
    @Select("SELECT * FROM face_feature WHERE user_id = #{userId} AND deleted = 0")
    FaceFeature findByUserId(@Param("userId") Long userId);
}
