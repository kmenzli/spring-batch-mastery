package com.infybuzz.config;

import com.infybuzz.listener.FirstJobExecutionListener;
import com.infybuzz.listener.FirstJobListener;
import com.infybuzz.listener.FirstStepListener;
import com.infybuzz.listener.SkipListener;
import com.infybuzz.model.StudentCsv;
import com.infybuzz.model.StudentJdbc;
import com.infybuzz.model.StudentJson;
import com.infybuzz.model.StudentXml;
import com.infybuzz.service.SecondTasklet;
import com.infybuzz.service.StudentService;
import com.infybuzz.step.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

    @Autowired
    DataSource dataSource;
    @Autowired
    private StudentService studentService;

    @Autowired
    private SecondItemProcessor toJsonItemProcessor;
    @Autowired
    private ThirdItemProcessor toXmlItemProcessor;
    @Autowired
    private FourthItemProcessor fromFlatFileToDbItemProcessor;

    @Autowired
    SkipListener skipListener;

    @Autowired
    @Qualifier("datasource")
    private DataSource datasource;

    @Autowired
    @Qualifier("universityDataSource")
    private DataSource universityDataSource;

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
                //.reader(jsonItemReader(null))
                //.reader(staxEventItemReader(null))
                //.reader(jdbcCursorItemReader())
                //.reader(itemReaderAdapter())
                //.processor(firstItemProcessor)
                //.processor(toJsonItemProcessor)
                //.processor(toXmlItemProcessor)
                .processor(fromFlatFileToDbItemProcessor)
                //.writer(thirdItemWriter)
                //.writer(flatFileItemWriter(null))
                //.writer(jsonFileItemWriter(null))
                //.writer(staxEventItemWriter(null))
                //--- Write to Db
                //.writer(jdbcBatchItemWriter())
                //-- Prepared JSDBC Writer
                .writer(preparedJdbcBatchItemWriter())
                //--- Skip les erreur
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(4)
                //--- On peut remplacer.skipLimit(Integer.maxValue) par une Policy
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                //---Listener qui declencher en cas d'erruer dans le Reader
                .retryLimit(1)
                .retry(Exception.class)
                .listener(skipListener)

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

    @Bean
    @StepScope
    public JsonItemReader<StudentJson> jsonItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        JsonItemReader<StudentJson> reader = new JsonItemReader<StudentJson>();
        reader.setResource(fileSystemResource);
        reader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(StudentJson.class)
        );
        reader.setMaxItemCount(8);
        //--- On skip les enregistrment 1 & 2 ==> on commence à lire à partir du 3 item
        reader.setCurrentItemCount(2);

        return reader;
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
        JdbcCursorItemReader reader = new JdbcCursorItemReader<StudentJdbc>();
        reader.setDataSource(universityDataSource);
        reader.setSql("select id, first_name as firstName, last_name as lastName, email from student");
        reader.setRowMapper(new BeanPropertyRowMapper() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });
        //-- charger uniquement les 8 premier elements
        reader.setMaxItemCount(8);
        //-- Commencer à lire à partir de la ligne 2 dns la DB
        reader.setCurrentItemCount(2);
        return reader;
    }
    //--- Lie à partir d'un WS

    /**
     * public ItemReaderAdapter<StudentResponse> itemReaderAdapter () {
     * ItemReaderAdapter<StudentResponse> reader = new ItemReaderAdapter<>();
     * reader.setTargetObject(studentService);
     * reader.setTargetMethod("readAvecParam");
     * //--- On utilise ça si on veut passer des param à notre WS
     * reader.setArguments(new Object[]{1L, "khemais"});
     * return reader ;
     * }
     */

    @Bean
    @StepScope
    public StaxEventItemReader<StudentXml> staxEventItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemReader<StudentXml> reader = new StaxEventItemReader<StudentXml>();
        reader.setResource(fileSystemResource);
        reader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXml.class);
            }

        });
        reader.setFragmentRootElementName("student");

        return reader;

    }

    @Bean
    @StepScope
    public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemWriter<StudentJdbc> writer = new FlatFileItemWriter<>();

        writer.setResource(fileSystemResource);
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id, firstName, LastName, Email");
            }
        });
        writer.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
            {
                setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
                    {
                        //--- Les valeurs dans le tableau doivent matcher exactement les propriétés dans le Bean
                        setNames(new String[]{"id", "firstName", "lastName", "email"});
                    }
                });
            }
        });
        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created @ :" + System.currentTimeMillis());
            }
        });
        return writer;
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<StudentJson> jsonFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        JsonFileItemWriter<StudentJson> writer =
                new JsonFileItemWriter<StudentJson>(fileSystemResource,
                        new JacksonJsonObjectMarshaller<StudentJson>());

        return writer;
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<StudentJson> simulateExceptionJsonFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        JsonFileItemWriter<StudentJson> writer =
                new JsonFileItemWriter<StudentJson>(fileSystemResource,
                        new JacksonJsonObjectMarshaller<StudentJson>()){
                    @Override
                    public String doWrite(List<? extends StudentJson> items) {
                        items.stream().forEach(item -> {
                            if (item.getId() == 2) {
                                System.out.println("Exception à hénérer Writer");
                                throw new NullPointerException("Exception");
                            }
                        });
                        return super.doWrite(items);
                    }
                };

        return writer;
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<StudentXml> staxEventItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemWriter<StudentXml> writer = new StaxEventItemWriter<StudentXml>();
        writer.setResource(fileSystemResource);
        writer.setRootTagName("students");
        writer.setMarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXml.class);
            }
        });

        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentCsv> writer = new JdbcBatchItemWriter<StudentCsv>();
        writer.setDataSource(universityDataSource);
        writer.setSql("insert into student (id, first_name, last_name, email) values (:id, :firstName, :lastName, :email)");
        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());
        return writer;
    }

    //--- PreparedStatement
    @Bean
    public JdbcBatchItemWriter<StudentCsv> preparedJdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentCsv> writer = new JdbcBatchItemWriter<StudentCsv>();
        writer.setDataSource(universityDataSource);
        writer.setSql("insert into student (id, first_name, last_name, email) values (?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<StudentCsv>() {
            @Override
            public void setValues(StudentCsv item, PreparedStatement ps) throws SQLException {
                ps.setLong(1, item.getId());
                ps.setString(2, item.getLastName());
                ps.setString(3, item.getLastName());
                ps.setString(4, item.getEmail());
            }
        });
        return writer;
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
