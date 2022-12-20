package com.example.batch.jobs.jobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.batch.jobs.jobListener.listener.JobLoggerListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(Step jobListenerStep) {
        return jobBuilderFactory.get("jobListenerJob")
            .incrementer(new RunIdIncrementer())
            .listener(new JobLoggerListener())
            .start(jobListenerStep)
            .build();
    }

    @JobScope
    @Bean
    public Step jobListenerStep(Tasklet jobListenerTasklet) {
        return stepBuilderFactory.get("jobListenerStep")
            .tasklet(jobListenerTasklet)
            .build();
    }

    @StepScope
    @Bean
    public Tasklet jobListenerTasklet() {
        return (contribution, chunkContext) -> {
            log.info("jobListener executed by Spring Batch");
            throw new IllegalArgumentException("job is failed");
            // return RepeatStatus.FINISHED;
        };
    }
}
