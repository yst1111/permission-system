package com.permission.mapper;

import com.permission.entity.Job;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface JobMapper {

    @Select("SELECT * FROM sys_job ORDER BY id")
    List<Job> selectJobList(Job job);

    @Select("SELECT * FROM sys_job WHERE id = #{id}")
    Job selectJobById(Long id);

    @Insert("INSERT INTO sys_job (job_name, job_group, cron_expression, target_method, target_params, status, description, create_time) " +
            "VALUES (#{jobName}, #{jobGroup}, #{cronExpression}, #{targetMethod}, #{targetParams}, #{status}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertJob(Job job);

    @Update("UPDATE sys_job SET job_name = #{jobName}, job_group = #{jobGroup}, cron_expression = #{cronExpression}, " +
            "target_method = #{targetMethod}, target_params = #{targetParams}, status = #{status}, description = #{description}, update_time = NOW() " +
            "WHERE id = #{id}")
    int updateJob(Job job);

    @Delete("DELETE FROM sys_job WHERE id = #{id}")
    int deleteJobById(Long id);

    @Update("UPDATE sys_job SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateJobStatus(@Param("id") Long id, @Param("status") Integer status);
}
