package com.permission.controller;

import com.permission.entity.JobLog;
import com.permission.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务执行记录Controller
 */
@RestController
@RequestMapping("/api/job-log")
public class JobLogController {

    @Autowired
    private JobLogService jobLogService;

    /**
     * 获取执行记录列表
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(required = false) String jobName) {
        
        JobLog query = new JobLog();
        query.setJobName(jobName);
        
        List<JobLog> list = jobLogService.getJobLogList(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", list);
        return result;
    }

    /**
     * 根据ID获取执行记录
     */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        JobLog jobLog = jobLogService.getJobLogById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", jobLog);
        return result;
    }

    /**
     * 删除执行记录
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        jobLogService.deleteJobLog(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
}
