package com.infybuzz.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before Step "+ stepExecution.getStepName());
        System.out.println("Before Step JobExecutionContext"+ stepExecution.getJobExecution().getExecutionContext());
        System.out.println("Before Step StepExecutionContext"+ stepExecution.getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("AFTER Step "+ stepExecution.getStepName());
        System.out.println("AFTER Step JobExecutionContext"+ stepExecution.getJobExecution().getExecutionContext());
        System.out.println("AFTER Step StepExecutionContext"+ stepExecution.getExecutionContext());
        return null;
    }
}
