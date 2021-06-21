package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.service.MethodInvokingExampleService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "methodInvokingJob")
public class MethodInvokingExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job methodInvokingJob() {
        return jobBuilderFactory.get("methodInvokingJob")
                .start(methodInvokingStep())
                .build();
    }

    @Bean
    public Step methodInvokingStep() {
        return stepBuilderFactory.get("methodInvokingStep")
                .tasklet(methodInvokingTasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public MethodInvokingTaskletAdapter methodInvokingTasklet(@Value("#{jobParameters['name']}") String name) {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = new MethodInvokingTaskletAdapter();
        methodInvokingTaskletAdapter.setTargetObject(methodInvokingExampleService());
        methodInvokingTaskletAdapter.setTargetMethod("sayMessage");
        methodInvokingTaskletAdapter.setArguments(new String[] {name});

        return methodInvokingTaskletAdapter;
    }

    @Bean
    public MethodInvokingExampleService methodInvokingExampleService() {
        return new MethodInvokingExampleService();
    }
}
