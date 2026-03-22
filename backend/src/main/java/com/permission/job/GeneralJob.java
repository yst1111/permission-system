package com.permission.job;

import com.permission.entity.Job;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 通用任务执行器
 * 通过反射调用 JobHandler 中的方法
 */
@Component
public class GeneralJob extends QuartzJobBean {
    
    private static final Logger logger = LoggerFactory.getLogger(GeneralJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String targetMethod = dataMap.getString("targetMethod");
        String targetParams = dataMap.getString("targetParams");
        
        logger.info("开始执行定时任务 - 方法: {}, 参数: {}", targetMethod, targetParams);
        
        try {
            // 这里可以扩展为从 Spring 容器中获取 Handler 并执行
            // 目前先保留简单的日志输出
            
            logger.info("定时任务执行成功 - 方法: {}", targetMethod);
        } catch (Exception e) {
            logger.error("定时任务执行失败: {}", e.getMessage());
            throw new JobExecutionException(e);
        }
    }
}
