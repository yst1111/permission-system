package com.permission.job;

import com.permission.entity.Job;
import com.permission.mapper.JobMapper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务初始化器
 * 自动扫描 JobHandler 并注册定时任务
 */
@Component
public class JobInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(JobInitializer.class);
    
    @Autowired
    private JobMapper jobMapper;
    
    @Autowired
    private Scheduler scheduler;
    
    // 定义任务处理器映射
    private static final Map<String, String> JOB_HANDLERS = new HashMap<>();
    
    static {
        // 在这里注册所有的定时任务处理器
        // key: 任务名称, value: 描述
        JOB_HANDLERS.put("sampleJob", "示例任务 - 每5秒执行一次");
        JOB_HANDLERS.put("sendEmail", "发送邮件任务");
        JOB_HANDLERS.put("syncData", "数据同步任务");
        JOB_HANDLERS.put("cleanLogs", "清理日志任务");
        JOB_HANDLERS.put("backupDatabase", "数据库备份任务");
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化定时任务...");
        
        for (Map.Entry<String, String> entry : JOB_HANDLERS.entrySet()) {
            String jobName = entry.getKey();
            String description = entry.getValue();
            
            // 检查是否已存在
            Job existingJob = new Job();
            existingJob.setJobName(jobName);
            List<Job> jobs = jobMapper.selectJobList(existingJob);
            
            if (jobs.isEmpty()) {
                // 创建新任务
                Job job = new Job();
                job.setJobName(jobName);
                job.setJobGroup("DEFAULT");
                job.setTargetMethod(jobName);
                job.setCronExpression(getCronForJob(jobName));
                job.setStatus(1); // 默认启用
                job.setDescription(description);
                
                jobMapper.insertJob(job);
                logger.info("创建定时任务: {}", jobName);
            }
            
            // 添加到调度器
            addJobToScheduler(jobName, "DEFAULT", getCronForJob(jobName), jobName, "");
        }
        
        logger.info("定时任务初始化完成!");
    }
    
    private String getCronForJob(String jobName) {
        // 根据任务名称返回不同的Cron表达式
        switch (jobName) {
            case "sampleJob":
                return "0/5 * * * * ?";
            case "sendEmail":
                return "0 0 * * * ?"; // 每小时
            case "syncData":
                return "0 0 0 * * ?"; // 每天
            case "cleanLogs":
                return "0 0 2 * * ?"; // 每天凌晨2点
            case "backupDatabase":
                return "0 0 3 * * ?"; // 每天凌晨3点
            default:
                return "0/10 * * * * ?"; // 默认10秒
        }
    }
    
    private void addJobToScheduler(String jobName, String jobGroup, String cronExpression, String targetMethod, String targetParams) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(GeneralJob.class)
                    .withIdentity(jobName, jobGroup)
                    .usingJobData("targetMethod", targetMethod)
                    .usingJobData("targetParams", targetParams)
                    .build();

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName + "_trigger", jobGroup)
                    .withSchedule(cronScheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("任务已添加到调度器: {}", jobName);
        } catch (Exception e) {
            logger.error("添加任务到调度器失败: {}", jobName, e);
        }
    }
}
