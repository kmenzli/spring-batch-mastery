package com.infybuzz.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("########## JobExecutionListener BEFORE");
        System.out.println("########## JobExecutionListener JobID "+ jobExecution.getJobId());
        System.out.println("########## JobExecutionListener StepExecution "+ jobExecution.getStepExecutions().toString());
        System.out.println("########## JobExecutionListener Status "+ jobExecution.getStatus());
        System.out.println("########## JobExecutionListener Paramters "+ jobExecution.getJobParameters());
        System.out.println("########## JobExecutionListener JobName "+ jobExecution.getJobInstance().getJobName());
        System.out.println("########## JobExecutionListener Job ExecutionContext "+ jobExecution.getExecutionContext());
        jobExecution.getExecutionContext().put("MyKey", "MyVlaue");
        jobExecution.getExecutionContext().put("MyKey1", "MyVlaue");
        jobExecution.getExecutionContext().put("MyKey2", "MyVlaue");
        jobExecution.getExecutionContext().put("MyKey3", "MyVlaue");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("########## JobExecutionListener AFTER");
        System.out.println("########## JobExecutionListener JobID "+ jobExecution.getJobId());
        System.out.println("########## JobExecutionListener StepExecution "+ jobExecution.getStepExecutions().toString());
        System.out.println("########## JobExecutionListener Status "+ jobExecution.getStatus());
        System.out.println("########## JobExecutionListener Paramters "+ jobExecution.getJobParameters());
        System.out.println("########## JobExecutionListener JobName "+ jobExecution.getJobInstance().getJobName());
        System.out.println("########## JobExecutionListener Job ExecutionContext "+ jobExecution.getExecutionContext());
    }
}
