package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.policy.RandomChunkSizePolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "randomChunkJob")
public class RandomChunkExampleConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job randomChunkJob() {
        return jobBuilderFactory.get("randomChunkJob")
                .start(randomChunkStep())
                .build();
    }

    @Bean
    public Step randomChunkStep() {
        return stepBuilderFactory.get("randomChunkStep")
                .<String, String>chunk(randomCompletionPolicy())
                .reader(randomChunkReader())
                .writer(randomChunkWriter())
                .build();
    }

    @Bean
    public CompletionPolicy randomCompletionPolicy() {
        return new RandomChunkSizePolicy();
    }

    @Bean
    public ItemWriter<String> randomChunkWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> current item = " + item);
            }
        };
    }

    @Bean
    public ItemReader<String> randomChunkReader() {
        List<String> items = IntStream.range(0, 100000)
                .mapToObj(num -> UUID.randomUUID().toString())
                .collect(Collectors.toList());

        return new ListItemReader<>(items);
    }
}
