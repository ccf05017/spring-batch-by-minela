package com.example.springbatch.minela.taklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class PromotionExampleFirstStep implements Tasklet {
    private final String NAME_KEY = "name";
    private final String HELLO = "Hello, %s";
    private final String SAVED = "%s saved in step context";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String name = (String) chunkContext
                .getStepContext()
                .getJobParameters()
                .get(NAME_KEY);

        ExecutionContext stepExecutionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getExecutionContext();
        stepExecutionContext.put(NAME_KEY, name);

        System.out.println(String.format(HELLO, name));
        System.out.println(String.format(SAVED, name));

        return RepeatStatus.FINISHED;
    }
}
