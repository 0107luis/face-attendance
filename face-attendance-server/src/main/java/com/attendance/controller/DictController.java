package com.attendance.controller;

import com.attendance.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/dict")
public class DictController {

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> dicts = Arrays.asList(
            createDict(1L, "考勤状态", "attendance_status"),
            createDict(2L, "请假类型", "leave_type"),
            createDict(3L, "申诉类型", "appeal_type")
        );
        return Result.success(dicts);
    }

    @GetMapping("/data/{dictCode}")
    public Result<List<Map<String, Object>>> getData(@PathVariable String dictCode) {
        List<Map<String, Object>> data = new ArrayList<>();
        if ("attendance_status".equals(dictCode)) {
            data.add(createDictData(1L, "正常", "normal", 1));
            data.add(createDictData(2L, "迟到", "late", 2));
            data.add(createDictData(3L, "早退", "early", 3));
            data.add(createDictData(4L, "缺勤", "absent", 4));
        } else if ("leave_type".equals(dictCode)) {
            data.add(createDictData(5L, "年假", "annual", 1));
            data.add(createDictData(6L, "病假", "sick", 2));
            data.add(createDictData(7L, "事假", "personal", 3));
            data.add(createDictData(8L, "调休", "compensate", 4));
        }
        return Result.success(data);
    }

    private Map<String, Object> createDict(Long id, String name, String code) {
        Map<String, Object> dict = new HashMap<>();
        dict.put("id", id);
        dict.put("dictName", name);
        dict.put("dictCode", code);
        dict.put("status", 1);
        return dict;
    }

    private Map<String, Object> createDictData(Long id, String label, String value, int sort) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("dictLabel", label);
        data.put("dictValue", value);
        data.put("sortOrder", sort);
        return data;
    }
}
