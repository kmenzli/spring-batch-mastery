package com.infybuzz.step;

import com.infybuzz.model.StudentJdbc;
import com.infybuzz.model.StudentJson;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SecondItemProcessor implements ItemProcessor<StudentJdbc, StudentJson> {
    @Override
    public StudentJson process(StudentJdbc item) throws Exception {
        System.out.println("Inside Processor ...");
        StudentJson s = new StudentJson();
        s.setId(item.getId());
        s.setFirstName(item.getFirstName());
        s.setLastName(item.getLastName());
        s.setEmail(item.getEmail());
        return s;
    }
}
