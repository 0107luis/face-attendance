package com.facial.recognition.service;

import com.facial.recognition.dto.FaceRecognitionRequest;
import com.facial.recognition.dto.FaceRecognitionResult;

public interface FaceRecognitionService {

    /**
     * 识别：上传人脸图片，返回匹配结果
     */
    FaceRecognitionResult recognize(FaceRecognitionRequest request);
}

