package com.example.batch.jobs.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatException;
import org.springframework.batch.repeat.RepeatOperations;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ChangeMessageJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job changeMessageJob(Step changeMessageStep) {
        return jobBuilderFactory.get("changeMessageJob")
                                .start(changeMessageStep)
                                .build();
    }

    @JobScope
    @Bean
    public Step changeMessageStep() {
        return stepBuilderFactory.get("changeMessageStep")
                                 .<String, String>chunk(3)
                                 .reader(new changeMessageReader())
                                 .processor((ItemProcessor<String, String>) item -> item + " is processing!")
                                 .writer(items -> {
                                     for (String item : items) {
                                         log.info("size is : {}", items.size());
                                         log.info("result is : {}", item);
                                     }
                                 })
                                 .listener(new ChangeMessageRepeat())
                                 .build();
    }

    public static class changeMessageReader implements ItemReader<String> {

        private String[] input = { "item1", "item2", "item3" };

        private int index = 0;

        @Override
        public String read() {
            if (index < input.length) {
                return input[index++];
            } else {
                return null;
            }
        }
    }

    public static class ChangeMessageRepeat implements RepeatOperations {
        @Override
        public RepeatStatus iterate(RepeatCallback callback) throws RepeatException {
            return RepeatStatus.FINISHED;
        }
    }
}