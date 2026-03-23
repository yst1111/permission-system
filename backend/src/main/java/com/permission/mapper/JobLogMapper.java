package com.permission.mapper;

import com.permission.entity.JobLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 定时任务执行记录Mapper
 */
@Mapper
public interface JobLogMapper {

    @Select("SELECT * FROM sys_job_log ORDER BY start_time DESC")
    List<JobLog> selectJobLogList();

    @Select("SELECT * FROM sys_job_log WHERE job_name LIKE CONCAT('%', #{jobName}, '%') ORDER BY start_time DESC")
    List<JobLog> selectJobLogListByName(@Param("jobName") String jobName);

    @Select("SELECT * FROM sys_job_log WHERE id = #{id}")
    JobLog selectJobLogById(Long id);

    @Insert("INSERT INTO sys_job_log (job_name, job_group, target_method, start_time, end_time, status, error_msg) " +
            "VALUES (#{jobName}, #{jobGroup}, #{targetMethod}, #{startTime}, #{endTime}, #{status}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertJobLog(JobLog jobLog);

    @Delete("DELETE FROM sys_job_log WHERE id = #{id}")
    int deleteJobLogById(Long id);
}
