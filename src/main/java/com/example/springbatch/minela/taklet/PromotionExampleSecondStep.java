package com.example.springbatch.minela.taklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class PromotionExampleSecondStep implements Tasklet {
    private final String NAME_KEY = "name";
    private final String FROM_FIRST_STEP = "%s is from first step";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobExecutionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        String name = (String) jobExecutionContext.get(NAME_KEY);

        System.out.println(String.format(FROM_FIRST_STEP, name));

        return RepeatStatus.FINISHED;
    }
}
