package com.example.springbatch.minela.configurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "callableJob")
public class CallableAdapterExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job callableJob() {
        return jobBuilderFactory.get("callableJob")
                .start(callableStep())
                .build();
    }

    @Bean
    public Step callableStep() {
        return stepBuilderFactory.get("callableStep")
                .tasklet(callableTasklet())
                .build();
    }

    @Bean
    @StepScope
    public Callable<RepeatStatus> callablePojo() {
        return () -> {
            System.out.println("This tasklet work in another thread.");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public CallableTaskletAdapter callableTasklet() {
        CallableTaskletAdapter callableTaskletAdapter = new CallableTaskletAdapter();
        callableTaskletAdapter.setCallable(callablePojo());

        return callableTaskletAdapter;
    }
}
