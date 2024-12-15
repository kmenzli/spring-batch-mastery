package com.infybuzz.config;

import com.infybuzz.listener.FirstJobExecutionListener;
import com.infybuzz.listener.FirstJobListener;
import com.infybuzz.listener.FirstStepListener;
import com.infybuzz.model.StudentCsv;
import com.infybuzz.service.SecondTasklet;
import com.infybuzz.step.FirstItemProcessor;
import com.infybuzz.step.FirstItemReader;
import com.infybuzz.step.FirstItemWriter;
import com.infybuzz.step.ThirdItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class SampleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SecondTasklet secondTasklet;

    @Autowired
    private FirstJobListener firstJobListener;

    @Autowired
    private FirstJobExecutionListener firstJobExecutionListener;

    @Autowired
    FirstStepListener firstStepListener;

    @Autowired
    FirstItemReader firstItemReader;

    @Autowired
    FirstItemWriter firstItemWriter;

    @Autowired
    FirstItemProcessor firstItemProcessor;

    @Autowired
    ThirdItemWriter thirdItemWriter;

    //@Bean
    public Job firstJob() {
        return jobBuilderFactory.get("First Job")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(secondStep())
                .listener(firstJobExecutionListener)
                .build();

    }

    //@Bean
    public Job secondJob() {
        return jobBuilderFactory.get("Second Job")
                .incrementer(new RunIdIncrementer())
                .start(firstchunStep())
                .next(secondStep())
                .build();

    }

    @Bean
    public Job sampleItemReaderJob() {
        return jobBuilderFactory.get("Reader Sample Job")
                .incrementer(new RunIdIncrementer())
                .start(readerSamplechunStep())
                .build();

    }

    public Step readerSamplechunStep() {
        return stepBuilderFactory.get("First Chunck Step")
                .<StudentCsv, StudentCsv>chunk(3)
                .reader(flatFileItemReader(null))
                //.processor(firstItemProcessor)
                .writer(thirdItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<StudentCsv> flatFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemReader<StudentCsv> reader = new FlatFileItemReader<>();

        reader.setResource(fileSystemResource);
        reader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {

                    {
                        setNames("ID", "First Name", "Last Name", "Email");
                    }
                    {
                        setDelimiter("|");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });
        reader.setLinesToSkip(1);
        return reader;
    }


    public Step firstchunStep() {
        return stepBuilderFactory.get("First Chunck Step")
                .<Integer, Long>chunk(3)
                .reader(firstItemReader)
                .processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }


    public Step firstStep() {
        return stepBuilderFactory.get("First Step")
                .tasklet(firstTasklet())
                .build();
    }

    public Step secondStep() {
        return stepBuilderFactory.get("Second Step")
                .tasklet(secondTasklet)
                .listener(firstStepListener)
                .build();
    }


    public Tasklet firstTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("This is our first Tasklet !!!");
                return RepeatStatus.FINISHED;
            }
        };
    }

    /**
     public Tasklet secondTasklet() {
     return new Tasklet() {
    @Override public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    System.out.println("This is our Second Tasklet !!!");
    return RepeatStatus.FINISHED;
    }
    };
     }
     */
}
