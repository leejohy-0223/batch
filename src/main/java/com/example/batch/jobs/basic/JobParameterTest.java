package com.example.batch.jobs.basic;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import lombok.RequiredArgsConstructor;

//@Component
@RequiredArgsConstructor
public class JobParameterTest implements ApplicationRunner {
    private final Job job;
    private final JobLauncher jobLauncher;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // job 파라미터 생성
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "lucid")
                .addLong("seq", 1L)
                .addDate("date", new Date())
                .addDouble("age", 26.5)
                .toJobParameters();

        // job 실행
        jobLauncher.run(job, jobParameters);
    }
}
