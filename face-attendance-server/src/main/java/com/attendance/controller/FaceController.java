package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.FaceFeature;
import com.attendance.service.FaceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/face")
public class FaceController {

    @Autowired
    private FaceFeatureService faceFeatureService;

    @PostMapping("/save")
    public Result<Void> saveFaceFeature(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String faceId = (String) params.get("faceId");
            String featureJson = (String) params.get("featureJson");
            
            FaceFeature existing = faceFeatureService.findByUserId(userId);
            if (existing != null) {
                faceFeatureService.updateFaceFeature(userId, featureJson);
            } else {
                faceFeatureService.saveFaceFeature(userId, faceId, featureJson);
            }
            return Result.success(null, "人脸特征保存成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/get/{userId}")
    public Result<FaceFeature> getFaceFeature(@PathVariable Long userId) {
        FaceFeature feature = faceFeatureService.findByUserId(userId);
        return Result.success(feature);
    }

    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteFaceFeature(@PathVariable Long userId) {
        faceFeatureService.deleteFaceFeature(userId);
        return Result.success(null, "人脸特征删除成功");
    }

    @GetMapping("/check/{userId}")
    public Result<Map<String, Object>> checkFaceStatus(@PathVariable Long userId) {
        FaceFeature feature = faceFeatureService.findByUserId(userId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("hasFace", feature != null);
        result.put("faceCount", feature != null ? feature.getFaceCount() : 0);
        return Result.success(result);
    }
}
