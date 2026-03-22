package com.permission.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class GeneralJob extends QuartzJobBean {
    
    private static final Logger logger = LoggerFactory.getLogger(GeneralJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String targetMethod = dataMap.getString("targetMethod");
        String targetParams = dataMap.getString("targetParams");
        
        logger.info("执行定时任务 - 方法: {}, 参数: {}", targetMethod, targetParams);
        
        // 这里可以添加任务执行的逻辑
        // 可以通过反射调用Service方法，或者直接写业务逻辑
        
        try {
            // 示例：打印任务执行日志
            System.out.println("定时任务执行成功！方法: " + targetMethod + ", 参数: " + targetParams);
        } catch (Exception e) {
            logger.error("定时任务执行失败: {}", e.getMessage());
            throw new JobExecutionException(e);
        }
    }
}
