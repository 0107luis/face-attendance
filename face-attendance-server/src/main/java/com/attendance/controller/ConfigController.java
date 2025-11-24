package com.attendance.controller;

import com.attendance.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final Map<String, Map<String, Object>> configs = new HashMap<>();

    public ConfigController() {
        configs.put("work_start_time", createConfig("work_start_time", "09:00:00", "上班时间"));
        configs.put("work_end_time", createConfig("work_end_time", "18:00:00", "下班时间"));
        configs.put("late_threshold", createConfig("late_threshold", "15", "迟到阈值(分钟)"));
        configs.put("face_similarity", createConfig("face_similarity", "0.88", "人脸相似度阈值"));
        configs.put("liveness_score", createConfig("liveness_score", "0.72", "活体检测阈值"));
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(new ArrayList<>(configs.values()));
    }

    @GetMapping("/{key}")
    public Result<Map<String, Object>> getByKey(@PathVariable String key) {
        return Result.success(configs.get(key));
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        String key = (String) params.get("configKey");
        configs.put(key, createConfig(key, (String) params.get("configValue"), (String) params.get("configName")));
        return Result.success(null, "更新成功");
    }

    private Map<String, Object> createConfig(String key, String value, String name) {
        Map<String, Object> config = new HashMap<>();
        config.put("configKey", key);
        config.put("configValue", value);
        config.put("configName", name);
        return config;
    }
}
