package com.attendance.service.impl;

import com.attendance.entity.FaceFeature;
import com.attendance.mapper.FaceFeatureMapper;
import com.attendance.service.FaceFeatureService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FaceFeatureServiceImpl extends ServiceImpl<FaceFeatureMapper, FaceFeature> implements FaceFeatureService {

    @Override
    public FaceFeature findByUserId(Long userId) {
        return baseMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public boolean saveFaceFeature(Long userId, String faceId, String featureJson) {
        FaceFeature feature = new FaceFeature();
        feature.setUserId(userId);
        feature.setFaceId(faceId);
        feature.setFeatureJson(featureJson);
        feature.setFaceCount(1);
        return save(feature);
    }

    @Override
    @Transactional
    public boolean updateFaceFeature(Long userId, String featureJson) {
        FaceFeature feature = findByUserId(userId);
        if (feature != null) {
            feature.setFeatureJson(featureJson);
            feature.setFaceCount(feature.getFaceCount() + 1);
            return updateById(feature);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteFaceFeature(Long userId) {
        return removeById(userId);
    }
}
