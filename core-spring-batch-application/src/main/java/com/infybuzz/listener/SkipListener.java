package com.infybuzz.listener;

import com.infybuzz.model.StudentCsv;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;

@Component
public class SkipListener {

    @OnSkipInRead
    public void slipInRead(Throwable th) {
        if (th instanceof FlatFileParseException) {
            createFile("/Users/menzli.khemais/WIP/workspace/kmenzli/back-office/udemy/batch/git/spring-batch-mastery/core-spring-batch-application/job/reader/readerException.txt",
                    ((FlatFileParseException) th).getInput());
        }

    }
    @OnSkipInProcess
    public void skipOnProcessor (StudentCsv s, Throwable e) {
        createFile("/Users/menzli.khemais/WIP/workspace/kmenzli/back-office/udemy/batch/git/spring-batch-mastery/core-spring-batch-application/job/reader/processorException.txt",
                s.getId() +" la cause est : "+ ((Exception) e).getMessage());

    }

    @OnSkipInWrite
    public void skipOnWriter (StudentCsv s, Throwable e) {
        createFile("/Users/menzli.khemais/WIP/workspace/kmenzli/back-office/udemy/batch/git/spring-batch-mastery/core-spring-batch-application/job/reader/writerException.txt",
                s.getId() +" la cause est : "+ ((Exception) e).getMessage());
    }

    /**
     * @OnSkipInWrite public void slipInWrite() {
     * <p>
     * }
     * @OnSkipInProcess public void slipInProcess() {
     * <p>
     * }
     */
    public void createFile(String filePath, String data) {
        try (FileWriter fileWriter = new FileWriter(new File(filePath), true)) {
            fileWriter.write(data+", "+ new Date()+ "\n");
        } catch (Exception e) {


        }
    }
}
