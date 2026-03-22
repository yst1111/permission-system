package com.permission.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Job {
    private Long id;
    private String jobName;
    private String jobGroup;
    private String cronExpression;
    private String targetMethod;
    private String targetParams;
    private Integer status;
    private String description;
    private Date createTime;
    private Date updateTime;
}
