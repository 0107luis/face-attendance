package com.attendance.service;

import com.attendance.entity.FaceFeature;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FaceFeatureService extends IService<FaceFeature> {
    
    FaceFeature findByUserId(Long userId);
    
    boolean saveFaceFeature(Long userId, String faceId, String featureJson);
    
    boolean updateFaceFeature(Long userId, String featureJson);
    
    boolean deleteFaceFeature(Long userId);
}
