package com.infybuzz.listener;

import org.springframework.stereotype.Component;

import javax.batch.api.listener.JobListener;

@Component
public class FirstJobListener implements JobListener {
    @Override
    public void beforeJob() throws Exception {
        System.out.println("########## JobListener BEFORE");
    }

    @Override
    public void afterJob() throws Exception {
        System.out.println("########## JobListener BEFORE");
    }
}
