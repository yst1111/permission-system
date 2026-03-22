package com.permission.service;

import com.permission.entity.Job;
import com.permission.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    public List<Job> getJobList(Job job) {
        return jobMapper.selectJobList(job);
    }

    public Job getJobById(Long id) {
        return jobMapper.selectJobById(id);
    }

    public int addJob(Job job) {
        return jobMapper.insertJob(job);
    }

    public int updateJob(Job job) {
        return jobMapper.updateJob(job);
    }

    public int deleteJob(Long id) {
        return jobMapper.deleteJobById(id);
    }

    public int changeStatus(Long id, Integer status) {
        return jobMapper.updateJobStatus(id, status);
    }
}
