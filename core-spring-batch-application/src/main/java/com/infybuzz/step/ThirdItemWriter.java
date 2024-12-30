package com.infybuzz.step;

import com.infybuzz.model.*;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThirdItemWriter implements ItemWriter<StudentJdbc> {

    @Override
    public void write(List<? extends StudentJdbc> list) throws Exception {
        System.out.println("Inside Item Writer...");
        list.stream().forEach(System.out::println);
    }
}
