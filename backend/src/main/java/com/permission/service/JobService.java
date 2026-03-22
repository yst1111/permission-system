package com.permission.service;

import com.permission.entity.Job;
import com.permission.mapper.JobMapper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;
    
    @Autowired
    private Scheduler scheduler;

    public List<Job> getJobList(Job job) {
        return jobMapper.selectJobList(job);
    }

    public Job getJobById(Long id) {
        return jobMapper.selectJobById(id);
    }

    public int addJob(Job job) {
        int result = jobMapper.insertJob(job);
        if (result > 0 && job.getStatus() == 1) {
            // 如果状态为启用，则添加到调度器
            addJobToScheduler(job);
        }
        return result;
    }

    public int updateJob(Job job) {
        Job oldJob = jobMapper.selectJobById(job.getId());
        int result = jobMapper.updateJob(job);
        if (result > 0) {
            try {
                // 先删除旧任务
                scheduler.deleteJob(new JobKey(oldJob.getJobName(), oldJob.getJobGroup()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 如果新状态为启用，则添加新任务
            if (job.getStatus() == 1) {
                addJobToScheduler(job);
            }
        }
        return result;
    }

    public int deleteJob(Long id) {
        Job job = jobMapper.selectJobById(id);
        int result = jobMapper.deleteJobById(id);
        if (result > 0 && job != null) {
            try {
                scheduler.deleteJob(new JobKey(job.getJobName(), job.getJobGroup()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public int changeStatus(Long id, Integer status) {
        Job job = jobMapper.selectJobById(id);
        int result = jobMapper.updateJobStatus(id, status);
        if (result > 0 && job != null) {
            if (status == 1) {
                // 启用任务
                addJobToScheduler(job);
            } else {
                // 禁用任务
                try {
                    scheduler.deleteJob(new JobKey(job.getJobName(), job.getJobGroup()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void addJobToScheduler(Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(GeneralJob.class)
                    .withIdentity(job.getJobName(), job.getJobGroup())
                    .usingJobData("targetMethod", job.getTargetMethod())
                    .usingJobData("targetParams", job.getTargetParams() != null ? job.getTargetParams() : "")
                    .build();

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getJobName() + "_trigger", job.getJobGroup())
                    .withSchedule(cronScheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
