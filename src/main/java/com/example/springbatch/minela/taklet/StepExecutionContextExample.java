package com.example.springbatch.minela.taklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class StepExecutionContextExample implements Tasklet {
    private final String HELLO_WORLD = "Hello, %s";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String name = (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("name");

        ExecutionContext stepExecutionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getExecutionContext();

        stepExecutionContext.put("name", name);

        return RepeatStatus.FINISHED;
    }
}
