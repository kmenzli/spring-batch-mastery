package com.infybuzz.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class SecondTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("This is our Second Tasklet !!!");
        System.out.println("This is our Second Tasklet !!!" +chunkContext.getStepContext().getJobExecutionContext());
        System.out.println("This is our Second Tasklet !!!" +chunkContext.getStepContext().getJobParameters());


        return null;
    }
}
