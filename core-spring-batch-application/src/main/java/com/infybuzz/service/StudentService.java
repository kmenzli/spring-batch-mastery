package com.infybuzz.service;

import com.infybuzz.model.StudentCsv;
import com.infybuzz.model.StudentResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    List<StudentResponse> list;
/**
    public List<StudentResponse> callRemoteWS() {

        RestTemplate restTemplate = new RestTemplate();
        StudentResponse[] students = restTemplate.getForObject("http://localhost:8081/api/v1/students",
                StudentResponse[].class);
        list = new ArrayList<>();
        for (int i = 0; i < students.length; i++) {
            list.add(students[i]);
        }

        return list;
    }
    public StudentResponse readSansParam() {
        if (list == null) {
            callRemoteWS();
        }
        if (list != null && !list.isEmpty()) {
            return list.remove(0);
        }
        return null;

    }

    public StudentResponse readAvecParam(Long id, String label) {
        System.out.println("ID = "+id+ " Label = "+ label);
        if (list == null) {
            callRemoteWS();
        }
        if (list != null && !list.isEmpty()) {
            return list.remove(0);
        }
        return null;

    }

    public StudentResponse create (StudentCsv studentCsv) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject("http://localhost:8081/api/v1/create",
                studentCsv,
                StudentResponse.class);
    }
 */
}
