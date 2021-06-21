package com.example.springbatch.minela.taklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class JobExecutionContextExample2 implements Tasklet {
    private final String HELLO_WORLD = "Hello, %s";
    private final String name;
    private final String fileName;

    public JobExecutionContextExample2(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobExecutionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        jobExecutionContext.put("name", name);

        System.out.println(String.format(HELLO_WORLD, name));

        return RepeatStatus.FINISHED;
    }
}
