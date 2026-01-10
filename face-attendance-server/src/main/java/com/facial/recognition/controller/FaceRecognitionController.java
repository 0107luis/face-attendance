package com.facial.recognition.controller;

import com.facial.recognition.dto.FaceRecognitionRequest;
import com.facial.recognition.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face-recognize")
@CrossOrigin(origins = "*")
public class FaceRecognitionController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @PostMapping
    public ResponseEntity<?> recognize(@RequestBody FaceRecognitionRequest request) {
        return ResponseEntity.ok(faceRecognitionService.recognize(request));
    }
}

