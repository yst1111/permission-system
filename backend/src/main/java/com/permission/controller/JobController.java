package com.permission.controller;

import com.permission.entity.Job;
import com.permission.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/list")
    public Map<String, Object> list(Job job) {
        List<Job> list = jobService.getJobList(job);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", job);
        return result;
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Job job) {
        jobService.addJob(job);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "添加成功");
        return result;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody Job job) {
        jobService.updateJob(job);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        jobService.deleteJob(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }

    @PutMapping("/status/{id}")
    public Map<String, Object> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        jobService.changeStatus(id, status);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", status == 1 ? "启用成功" : "禁用成功");
        return result;
    }
}
