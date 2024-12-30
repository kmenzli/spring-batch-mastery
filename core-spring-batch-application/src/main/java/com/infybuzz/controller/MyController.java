/*
package com.infybuzz.controller;

import com.infybuzz.model.JobParamRequest;
import com.infybuzz.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/job")
public class MyController {



    @Autowired
    JobService jobService;

    @Autowired
    JobOperator jobOperator;

    @GetMapping("start/{jobName}")
    public String startJob(@PathVariable String jobName, @RequestBody List<JobParamRequest> params) throws Exception {
        jobService.runJob(jobName, params);
        return "JobStarter ...";
    }

    @GetMapping("stop/{executionId}")
    public String stop(@PathVariable String executionId) {
        try {
            jobOperator.stop(Long.valueOf(executionId));
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "Stoping job "+executionId+" ...";
    }
}
*/
