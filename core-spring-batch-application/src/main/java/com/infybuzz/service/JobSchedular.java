package com.infybuzz.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobSchedular {
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    Job secondJob;

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void schedular () {
        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("currentTime", new JobParameter(System.currentTimeMillis()));

        JobParameters jobParameters = new JobParameters(parameters);
        JobExecution jobExecution = null;
        try {
                jobExecution = jobLauncher.run(secondJob, jobParameters);

            System.out.println("Job Execution ID :" + jobExecution.getJobId());
        } catch (Exception ex) {

        }

    }
}
