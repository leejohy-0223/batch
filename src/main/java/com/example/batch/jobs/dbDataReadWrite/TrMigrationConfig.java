package com.example.batch.jobs.dbDataReadWrite;

import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import com.example.batch.core.domain.accounts.Accounts;
import com.example.batch.core.domain.accounts.AccountsRepository;
import com.example.batch.core.domain.orders.Orders;
import com.example.batch.core.domain.orders.OrdersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // READ
    private final OrdersRepository ordersRepository;

    // WRITE
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
            .incrementer(new RunIdIncrementer())
            .start(trMigrationStep)
            .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(
        ItemReader<Orders> trOrdersReader,
        ItemProcessor<Orders, Accounts> trOrderProcessor,
        ItemWriter<Accounts> trOrdersWriter) {

        // return stepBuilderFactory.get("trMigrationStep")
        //     .<Orders, Orders>chunk(5) // READ : Orders, WRITE : Orders -> 5개 단위로 커밋을 하겠다. 트랜잭션을 chunk 단위로 처리
        //     .reader(trOrdersReader)
        //     .writer(items -> items.forEach(System.out::println))
        //     .build();

        return stepBuilderFactory.get("trMigrationStep")
            .<Orders, Accounts>chunk(5) // READ : Orders, WRITE : Orders -> 5개 단위로 커밋을 하겠다. 트랜잭션을 chunk 단위로 처리
            .reader(trOrdersReader)
            .processor(trOrderProcessor)
            .writer(trOrdersWriter)
            .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
            .name("trOrdersReader")
            .repository(ordersRepository)
            .methodName("findAll")
            .pageSize(5) // == chunk Size
            .arguments(List.of()) // 메서드 파라미터
            .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
            .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return Accounts::new;
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrdersWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
            .repository(accountsRepository)
            .methodName("save")
            .build();
    }
}
