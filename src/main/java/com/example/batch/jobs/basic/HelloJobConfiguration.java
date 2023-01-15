package com.example.batch.jobs.basic;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // 잡 구성
    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
                                .start(helloStep1())
                                .build();
    }

    @Bean
    public Step helloStep1() {
        return stepBuilderFactory.get("helloStep1")
                                 .tasklet((contribution, chunkContext) -> {
                                     JobParameters jobParameters =
                                             contribution.getStepExecution().getJobExecution()
                                                         .getJobParameters();

                                     Date date = jobParameters.getDate("date");
                                     Double age = jobParameters.getDouble("age");
                                     Long seq = jobParameters.getLong("seq");
                                     String name = jobParameters.getString("name");

                                     log.info("date = {}", date);
                                     log.info("age = {}", age);
                                     log.info("seq = {}", seq);
                                     log.info("name = {}", name);

                                     return RepeatStatus.FINISHED;
                                 })
                                 .build();
    }

}
