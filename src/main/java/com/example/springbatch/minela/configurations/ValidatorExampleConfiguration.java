package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.incremeter.DateIncrementer;
import com.example.springbatch.minela.listener.JobLoggerListener;
import com.example.springbatch.minela.validators.FilenameValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "job")
@Configuration
public class ValidatorExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator compositeJobParametersValidator = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
                new String[] {"fileName"},  // 필수로 존재해야 하는 파라미터 검증 key
                new String[] {"name", "currentDate"}       // 없어도 되는 파라미터 검증 key
        );
        defaultJobParametersValidator.afterPropertiesSet();     // 필수 key, optional key가 겹치는지 확인한다.

        compositeJobParametersValidator.setValidators(Arrays.asList(
                defaultJobParametersValidator, new FilenameValidator()
        ));

        return compositeJobParametersValidator;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .validator(validator())
                .listener(new JobLoggerListener())
                .incrementer(new DateIncrementer())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .tasklet(helloWorldTasklet(null, null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name,
                                     @Value("#{jobParameters['fileName']}") String fileName) {
        return (contribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s", name));
            System.out.println(String.format("Filename is %s", fileName));
            return RepeatStatus.FINISHED;
        };
    }

    // JobExecutionContext 예제
//    @Bean
//    @StepScope
//    public Tasklet helloWorldTasklet() {
//        return new JobExecutionContextExample();
//    }

    // JobExecutionContext2 예제
//    @Bean
//    @StepScope
//    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name,
//                                     @Value("#{jobParameters['fileName']}") String fileName) {
//        return new JobExecutionContextExample2(name, fileName);
//    }

    // StepExecutionContext 예제
//    @Bean
//    @StepScope
//    public Tasklet helloWorldTasklet() {
//        return new StepExecutionContextExample();
//    }
}
