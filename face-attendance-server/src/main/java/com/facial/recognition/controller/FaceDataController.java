package com.facial.recognition.controller;

import com.facial.recognition.dto.FaceDataRequest;
import com.facial.recognition.pojo.FaceData;
import com.facial.recognition.service.FaceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/face-data")
@CrossOrigin(origins = "*")
public class FaceDataController {

    @Autowired
    private FaceDataService faceDataService;

    @PostMapping
    public ResponseEntity<?> saveFaceData(@RequestBody FaceDataRequest request,
                                          @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = request.getUserId() != null ? request.getUserId() : headerUserId;
        Map<String, Object> resp = new HashMap<>();
        if (userId == null || userId <= 0) {
            resp.put("message", "用户ID不能为空");
            return ResponseEntity.badRequest().body(resp);
        }
        if (request.getFaceImage() == null || request.getFaceImage().isBlank()) {
            resp.put("message", "人脸图像数据不能为空");
            return ResponseEntity.badRequest().body(resp);
        }

        try {
            FaceData saved = faceDataService.saveOrUpdate(userId, request.getFaceImage());
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            resp.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception ex) {
            resp.put("message", "保存人脸数据失败");
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyFaceData(@RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        if (headerUserId == null || headerUserId <= 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "缺少用户身份信息");
            return ResponseEntity.badRequest().body(resp);
        }
        Optional<FaceData> data = faceDataService.findByUserId(headerUserId);
        return data.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFaceDataByUser(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "无效的用户ID");
            return ResponseEntity.badRequest().body(resp);
        }
        Optional<FaceData> data = faceDataService.findByUserId(userId);
        return data.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

