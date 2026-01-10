package com.facial.recognition.service;

import com.facial.recognition.pojo.FaceData;

import java.util.Optional;

public interface FaceDataService {

    /**
     * 保存或更新用户人脸数据（传入Base64图片，服务端调用特征提取）
     */
    FaceData saveOrUpdate(Integer userId, String faceImageBase64);

    /**
     * 根据用户ID获取人脸数据
     */
    Optional<FaceData> findByUserId(Integer userId);
}

