package com.infybuzz.step;

import com.infybuzz.model.StudentJdbc;
import com.infybuzz.model.StudentJson;
import com.infybuzz.model.StudentXml;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ThirdItemProcessor implements ItemProcessor<StudentJdbc, StudentXml> {
    @Override
    public StudentXml process(StudentJdbc item) throws Exception {
        System.out.println("Inside Processor ...");
        StudentXml s = new StudentXml();
        s.setId(item.getId());
        s.setFirstName(item.getFirstName());
        s.setLastName(item.getLastName());
        s.setEmail(item.getEmail());
        return s;
    }
}
