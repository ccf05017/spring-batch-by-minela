package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.taklet.PromotionExampleFirstStep;
import com.example.springbatch.minela.taklet.PromotionExampleSecondStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "promotionJob")
public class PromotionBatchExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job promotionJob() {
        return jobBuilderFactory.get("promotionJob")
                .start(promotionStep1())
                .next(promotionStep2())
                .build();
    }

    @Bean
    public Step promotionStep1() {
        return stepBuilderFactory.get("promotionStep1")
                .tasklet(new PromotionExampleFirstStep())
                .listener(promotionListener())
                .build();
    }

    @Bean
    public Step promotionStep2() {
        return stepBuilderFactory.get("promotionStep2")
                .tasklet(new PromotionExampleSecondStep())
                .build();
    }

    @Bean
    public StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener executionContextPromotionListener = new ExecutionContextPromotionListener();
        executionContextPromotionListener.setKeys(new String[]{"name"});

        return executionContextPromotionListener;
    }
}

