package com.example.springbatch.minela.validators;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class FilenameValidator implements JobParametersValidator {
    private static final String FILE_NAME_KEY = "fileName";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString(FILE_NAME_KEY);

        if (!StringUtils.hasText(fileName)) {
            throw new JobParametersInvalidException("fileName 파라미터는 필수입니다.");
        }
        if (!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
            throw new JobParametersInvalidException("file 확장자는 반드시 csv이어야만 합니다.");
        }
    }
}
