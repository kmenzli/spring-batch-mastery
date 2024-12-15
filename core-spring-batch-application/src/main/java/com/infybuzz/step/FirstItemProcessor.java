package com.infybuzz.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {
    @Override
    public Long process(Integer integer) throws Exception {
        System.out.println("Inside Processor ...");
        return Long.valueOf(integer + 20);
    }
}
