package group.u.mdas.config;

import group.u.mdas.batch.writer.BatchWriter;
import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.service.CompanyImportService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<CompanyIdentifier> reader() {
        return new FlatFileItemReaderBuilder<CompanyIdentifier>()
                .name("companyItemReader")
                .resource(new ClassPathResource("companylist.csv"))
                .delimited()
                .includedFields(new Integer[]{0, 1, 6, 7})
                .names(new String[]{"symbol", "name", "sector", "industry"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(CompanyIdentifier.class);
                }})
                .build();
    }

    @Bean
    public CompanyItemProcessor processor() {
        return new CompanyItemProcessor();
    }

    @Bean
    public BatchWriter<CompanyIdentifier> writer(CompanyImportService importService ) {
        return new BatchWriter<>(importService);
    }

    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importCompany")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener(ThreadPoolTaskExecutor executor) {
        return new JobExecutionListener() {
            private ThreadPoolTaskExecutor taskExecutor = executor;
            @Override
            public void beforeJob(JobExecution jobExecution) {

            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                taskExecutor.shutdown();
            }
        };
    }

    @Bean
    public CustomStepListener customStepListener(ThreadPoolTaskExecutor executor){
        return new CustomStepListener(executor);
    }

    @Bean
    public Step step1(BatchWriter<CompanyIdentifier> writer, CustomStepListener listener) {
        return stepBuilderFactory.get("step1")
                .<CompanyIdentifier, CompanyIdentifier>chunk(20)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .listener(listener)
                .build();
    }
    // end::jobstep[]
}
