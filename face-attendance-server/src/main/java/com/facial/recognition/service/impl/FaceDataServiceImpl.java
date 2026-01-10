package com.facial.recognition.service.impl;

import com.facial.recognition.dto.FaceEmbeddingResult;
import com.facial.recognition.pojo.FaceData;
import com.facial.recognition.service.FaceEmbeddingClient;
import com.facial.recognition.repository.FaceDataRepository;
import com.facial.recognition.service.FaceDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class FaceDataServiceImpl implements FaceDataService {

    @Autowired
    private FaceDataRepository faceDataRepository;

    @Autowired
    private FaceEmbeddingClient faceEmbeddingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public FaceData saveOrUpdate(Integer userId, String faceImageBase64) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (faceImageBase64 == null || faceImageBase64.isBlank()) {
            throw new IllegalArgumentException("人脸图像不能为空");
        }

        byte[] imageBytes = decodeBase64Image(faceImageBase64);
        FaceEmbeddingResult embeddingResult = faceEmbeddingClient.fetchEmbedding(imageBytes);
        if (embeddingResult == null || !embeddingResult.isHasFace()) {
            throw new IllegalArgumentException("未检测到人脸，请重新上传清晰的人脸照片");
        }

        String embeddingJson = serializeEmbedding(embeddingResult.getEmbedding());

        Optional<FaceData> existing = faceDataRepository.findByUserId(userId);
        FaceData entity = existing.orElseGet(FaceData::new);
        entity.setUserId(userId);
        entity.setFaceTemplate(embeddingJson);
        entity.setCreatedDate(LocalDateTime.now());
        return faceDataRepository.save(entity);
    }

    @Override
    public Optional<FaceData> findByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            return Optional.empty();
        }
        return faceDataRepository.findByUserId(userId);
    }

    private byte[] decodeBase64Image(String faceImageBase64) {
        String cleaned = faceImageBase64.trim();
        int commaIndex = cleaned.indexOf(',');
        if (commaIndex > 0) {
            cleaned = cleaned.substring(commaIndex + 1);
        }
        try {
            return Base64.getDecoder().decode(cleaned);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("人脸图像Base64解析失败", ex);
        }
    }

    private String serializeEmbedding(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化人脸特征失败", e);
        }
    }
}

