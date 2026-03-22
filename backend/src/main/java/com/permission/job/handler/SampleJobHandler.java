package com.permission.job.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 示例任务处理器
 * 使用方法：在定时任务管理中填写 targetMethod = sampleJob
 */
@Component
public class SampleJobHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(SampleJobHandler.class);
    
    /**
     * 示例任务
     */
    public void sampleJob() {
        logger.info("执行示例任务 SampleJob");
        System.out.println("这是示例任务，每隔5秒执行一次！");
    }
    
    /**
     * 发送邮件任务
     */
    public void sendEmail() {
        logger.info("执行发送邮件任务");
        System.out.println("发送邮件任务执行中...");
    }
    
    /**
     * 数据同步任务
     */
    public void syncData() {
        logger.info("执行数据同步任务");
        System.out.println("数据同步任务执行中...");
    }
    
    /**
     * 清理日志任务
     */
    public void cleanLogs() {
        logger.info("执行清理日志任务");
        System.out.println("清理日志任务执行中...");
    }
    
    /**
     * 备份数据库任务
     */
    public void backupDatabase() {
        logger.info("执行数据库备份任务");
        System.out.println("数据库备份任务执行中...");
    }
}
