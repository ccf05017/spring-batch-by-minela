package com.example.springbatch.minela.configurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.core.step.tasklet.SystemProcessExitCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "systemCommandJob")
public class SystemCommandExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job systemCommandJob() {
        return jobBuilderFactory.get("systemCommandJob")
                .start(systemCommandStep())
                .build();
    }

    @Bean
    public Step systemCommandStep() {
        return stepBuilderFactory.get("systemCommandStep")
                .tasklet(systemCommandTasklet())
                .build();
    }

    @Bean
    public SystemCommandTasklet systemCommandTasklet() {
        SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();

        systemCommandTasklet.setWorkingDirectory("/Users/poppo/Documents/Development/Toys");
        systemCommandTasklet.setCommand("touch tmp.txt");
        systemCommandTasklet.setInterruptOnCancel(true);
        systemCommandTasklet.setSystemProcessExitCodeMapper(touch());
        systemCommandTasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
        systemCommandTasklet.setTerminationCheckInterval(1000);
        systemCommandTasklet.setTimeout(5000);

        return systemCommandTasklet;
    }

    @Bean
    public SystemProcessExitCodeMapper touch() {
        return new SimpleSystemProcessExitCodeMapper();
    }
}
