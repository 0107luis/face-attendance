package com.facial.recognition.service;

import com.facial.recognition.dto.FaceEmbeddingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

/**
 * 调用 Python 人脸特征微服务
 */
@Component
public class FaceEmbeddingClient {

    private final RestTemplate restTemplate;

    @Value("${facemind.face-service-url:http://127.0.0.1:8000/embed}")
    private String faceServiceUrl;

    public FaceEmbeddingClient(RestTemplateBuilder restTemplateBuilder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(15).toMillis());

        this.restTemplate = restTemplateBuilder
                .requestFactory(() -> factory)
                .build();
    }

    public FaceEmbeddingResult fetchEmbedding(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource resource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "face.jpg";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<FaceEmbeddingResult> resp = restTemplate.exchange(
                    faceServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    FaceEmbeddingResult.class
            );

            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("调用人脸特征服务失败，状态码：" + resp.getStatusCode());
            }
            return resp.getBody();
        } catch (RestClientException ex) {
            throw new IllegalStateException("调用人脸特征服务异常: " + ex.getMessage(), ex);
        }
    }
}

