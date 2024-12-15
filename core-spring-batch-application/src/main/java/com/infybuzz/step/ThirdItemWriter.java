package com.infybuzz.step;

import com.infybuzz.model.StudentCsv;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThirdItemWriter implements ItemWriter<StudentCsv> {

    @Override
    public void write(List<? extends StudentCsv> list) throws Exception {
        System.out.println("Inside Item Writer...");
        list.stream().forEach(System.out::println);
    }
}
