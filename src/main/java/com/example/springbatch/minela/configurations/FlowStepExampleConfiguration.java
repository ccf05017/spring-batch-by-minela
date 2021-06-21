package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.policy.RandomDeciderPolicy;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.batch.api.Decider;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "flowStepJob")
public class FlowStepExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowStepJob() {
        return jobBuilderFactory.get("flowStepJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(randomDecider())
                .from(randomDecider()).on("FAILED").to(failStep())
                .from(randomDecider()).on("*").to(successStep())
                .end() // flow 스텝을 구성할 때는 반드시 끝을 선언해줘야 한다.
                .build();
    }

    @Bean
    public JobExecutionDecider randomDecider() {
        return new RandomDeciderPolicy();
    }

    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .tasklet(passTasklet())
                .build();
    }

    @Bean
    public Tasklet passTasklet() {
        return (contribution, chunkContext) -> RepeatStatus.FINISHED;
    }

    @Bean
    public Step successStep() {
        return stepBuilderFactory.get("successStep")
                .tasklet(successTasklet())
                .build();
    }

    @Bean
    public Tasklet successTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Success!!");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step failStep() {
        return stepBuilderFactory.get("failStep")
                .tasklet(failTasklet())
                .build();
    }

    @Bean
    public Tasklet failTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Fail!!");
            return RepeatStatus.FINISHED;
        };
    }
}
