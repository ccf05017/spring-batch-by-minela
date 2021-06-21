package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.taklet.ExploringTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "exploringJob")
public class ExploringTaskletExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobExplorer jobExplorer;

    @Bean
    public Job exploringJob() {
        return jobBuilderFactory.get("exploringJob")
                .start(exploringStep())
                .build();
    }

    @Bean
    public Step exploringStep() {
        return stepBuilderFactory.get("exploringStep")
                .tasklet(new ExploringTasklet(jobExplorer))
                .build();
    }
}
