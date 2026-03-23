package com.permission.service;

import com.permission.entity.JobLog;
import com.permission.mapper.JobLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 定时任务执行记录Service
 */
@Service
public class JobLogService {

    @Autowired
    private JobLogMapper jobLogMapper;

    /**
     * 获取执行记录列表
     */
    public List<JobLog> getJobLogList(JobLog jobLog) {
        if (jobLog != null && jobLog.getJobName() != null && !jobLog.getJobName().isEmpty()) {
            return jobLogMapper.selectJobLogListByName(jobLog.getJobName());
        }
        return jobLogMapper.selectJobLogList();
    }

    /**
     * 根据ID获取执行记录
     */
    public JobLog getJobLogById(Long id) {
        return jobLogMapper.selectJobLogById(id);
    }

    /**
     * 新增执行记录
     */
    public int addJobLog(JobLog jobLog) {
        return jobLogMapper.insertJobLog(jobLog);
    }

    /**
     * 删除执行记录
     */
    public int deleteJobLog(Long id) {
        return jobLogMapper.deleteJobLogById(id);
    }

    /**
     * 记录任务开始执行
     */
    public void recordJobStart(String jobName, String jobGroup, String targetMethod) {
        JobLog log = new JobLog();
        log.setJobName(jobName);
        log.setJobGroup(jobGroup);
        log.setTargetMethod(targetMethod);
        log.setStartTime(new Date());
        log.setStatus("RUNNING");
        jobLogMapper.insertJobLog(log);
    }

    /**
     * 记录任务执行结束
     */
    public void recordJobEnd(String jobName, String jobGroup, String targetMethod, boolean success, String errorMsg) {
        List<JobLog> logs = jobLogMapper.selectJobLogListByName(jobName);
        
        // 找到最近的一次执行记录
        if (!logs.isEmpty()) {
            JobLog latestLog = logs.get(0);
            latestLog.setEndTime(new Date());
            latestLog.setStatus(success ? "SUCCESS" : "FAIL");
            latestLog.setErrorMsg(errorMsg);
            
            // 计算耗时
            if (latestLog.getStartTime() != null && latestLog.getEndTime() != null) {
                long duration = (latestLog.getEndTime().getTime() - latestLog.getStartTime().getTime()) / 1000;
                latestLog.setDuration(duration);
            }
        }
    }
}
