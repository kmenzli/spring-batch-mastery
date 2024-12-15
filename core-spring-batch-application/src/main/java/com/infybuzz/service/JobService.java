package com.infybuzz.service;

import com.infybuzz.model.JobParamRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job firstJob;

    @Autowired
    Job secondJob;
    @Async
    public void runJob (String jobName, List<JobParamRequest> paramRequestList) {
        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("currentTime", new JobParameter(System.currentTimeMillis()));
        paramRequestList.stream().forEach( p -> {
            parameters.put(p.getParamKey(),
                    new JobParameter(p.getParamValue()));
        } );

        JobParameters jobParameters = new JobParameters(parameters);
        JobExecution jobExecution = null;
        try {
            if (jobName.equalsIgnoreCase("first")) {
                jobExecution = jobLauncher.run(firstJob, jobParameters);
            } else if (jobName.equalsIgnoreCase("second")) {
                jobExecution = jobLauncher.run(secondJob, jobParameters);
            }
            System.out.println("Job Execution ID :" + jobExecution.getJobId());
        } catch (Exception ex) {

        }
    }
}
