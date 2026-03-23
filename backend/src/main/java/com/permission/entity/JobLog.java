package com.permission.entity;

import lombok.Data;
import java.util.Date;

/**
 * 定时任务执行记录实体
 */
@Data
public class JobLog {
    private Long id;
    private String jobName;
    private String jobGroup;
    private String targetMethod;
    private Date startTime;
    private Date endTime;
    private String status;
    private String errorMsg;
    private Date createTime;
    
    // 耗时（秒）
    private Long duration;
}
