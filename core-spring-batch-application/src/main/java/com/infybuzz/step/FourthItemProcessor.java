package com.infybuzz.step;

import com.infybuzz.model.StudentCsv;
import com.infybuzz.model.StudentJdbc;
import com.infybuzz.model.StudentXml;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FourthItemProcessor implements ItemProcessor<StudentCsv, StudentCsv> {
    @Override
    public StudentCsv process(StudentCsv item) throws Exception {
        System.out.println("Inside Processor ...");
        if (item.getId() == 8) {
            System.out.println("Exception à générer Processor..");
            throw new Exception("Tneeekit");
        }
        StudentCsv s = new StudentCsv();
        s.setId(item.getId());
        s.setFirstName(item.getFirstName());
        s.setLastName(item.getLastName()+" Modified");
        s.setEmail(item.getEmail());
        return s;
    }
}
