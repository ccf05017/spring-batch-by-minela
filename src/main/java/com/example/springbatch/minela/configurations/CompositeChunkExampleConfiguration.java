package com.example.springbatch.minela.configurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "compositeChunkJob")
public class CompositeChunkExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /*
    fixtedChunkJob과 실행 후 비교하면 step의 commit 수가 분명히 다르다.
    TimeoutTerminationPolicy가 정상적으로 반영됨을 알 수 있다.
     */
    @Bean
    public Job compositeChunkJob() {
        return jobBuilderFactory.get("compositeChunkJob")
                .start(compositeChunkStep())
                .build();
    }

    @Bean
    public Step compositeChunkStep() {
        return stepBuilderFactory.get("compositeChunkStep")
                .<String, String>chunk(compositePolicy())
                .reader(compositeChunkReader())
                .writer(compositeChunkWriter())
                .build();
    }

    @Bean
    public CompletionPolicy compositePolicy() {
        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();

        compositeCompletionPolicy.setPolicies(new CompletionPolicy[]{
                new TimeoutTerminationPolicy(3),
                new SimpleCompletionPolicy(1000)
        });

        return compositeCompletionPolicy;
    }

    @Bean
    public ListItemReader<String> compositeChunkReader() {
        List<String> items = IntStream.range(0, 100000)
                .mapToObj(num -> UUID.randomUUID().toString())
                .collect(Collectors.toList());

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> compositeChunkWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> current item = " + item);
            }
        };
    }
}
