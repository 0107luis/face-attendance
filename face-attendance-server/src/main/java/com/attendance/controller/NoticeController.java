package com.attendance.controller;

import com.attendance.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    private final List<Map<String, Object>> notices = new ArrayList<>();

    public NoticeController() {
        notices.add(createNotice(1L, "系统升级通知", "系统将于今晚22:00进行升级维护", 1));
        notices.add(createNotice(2L, "考勤规则调整", "本月起迟到阈值调整为15分钟", 2));
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(notices);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(createNotice(id, "通知" + id, "内容", 1));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        return Result.success(null, "添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        return Result.success(null, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return Result.success(null, "删除成功");
    }

    private Map<String, Object> createNotice(Long id, String title, String content, int type) {
        Map<String, Object> notice = new HashMap<>();
        notice.put("id", id);
        notice.put("title", title);
        notice.put("content", content);
        notice.put("noticeType", type);
        notice.put("status", 1);
        notice.put("createTime", new Date());
        return notice;
    }
}
