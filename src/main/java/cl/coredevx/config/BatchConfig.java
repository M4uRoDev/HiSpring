package cl.coredevx.config;

import cl.coredevx.model.Persona;
import cl.coredevx.writer.ConsoleItemWriter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Persona> reader() {
        return new FlatFileItemReaderBuilder<Persona>()
                .name("personaItemReader")
                .resource(new ClassPathResource("data.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "nombre", "correo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Persona.class);
                }})
                .build();
    }

    @Bean
    public ItemWriter<Persona> writer() {
        return new ConsoleItemWriter();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("csvJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step-csv-to-console", jobRepository)
                .<Persona, Persona>chunk(5, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }

}
