package com.example.springbatch.minela.configurations;

import com.example.springbatch.minela.listener.StepLoggerListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "fixedChunkJob")
public class FixedChunkExampleConfiguration {
    private static final int CHUNK_SIZE = 1000;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedChunkJob() {
        return jobBuilderFactory.get("fixedChunkJob")
                .start(fixedChunkStep())
                .build();
    }

    @Bean
    public Step fixedChunkStep() {
        return stepBuilderFactory.get("fixedChunkStep")
                .<String, String>chunk(CHUNK_SIZE)
                .reader(fixedChunkItemReader())
                .writer(fixedChunkItemWriter())
                .listener(new StepLoggerListener())
                .build();
    }

    @Bean
    public ListItemReader<String> fixedChunkItemReader() {
        List<String> items = IntStream.range(0, 100000)
                .mapToObj(num -> UUID.randomUUID().toString())
                .collect(Collectors.toList());

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> fixedChunkItemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> current item = " + item);
            }
        };
    }
}
