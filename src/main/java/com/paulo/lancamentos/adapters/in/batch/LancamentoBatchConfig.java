package com.paulo.lancamentos.adapters.in.batch;

import com.paulo.lancamentos.domain.model.Lancamento;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LancamentoBatchConfig {

    private static final int CHUNK_SIZE = 1000;

    @Bean
    @StepScope
    public FlatFileItemReader<String[]> lancamentoReader(
            @Value("#{jobParameters['caminho']}") String caminho) {
        return new FlatFileItemReaderBuilder<String[]>()
                .name("lancamentoReader")
                .resource(new FileSystemResource(caminho))
                .lineMapper((line, lineNumber) -> line.split(","))
                .build();
    }

    @Bean
    public Step lancamentoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<String[]> reader,
                               LancamentoItemProcessor processor,
                               LancamentoItemWriter writer) {
        return new StepBuilder("lancamentoStep", jobRepository)
                .<String[], Lancamento>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job lancamentoJob(JobRepository jobRepository, Step lancamentoStep) {
        return new JobBuilder("lancamentoJob", jobRepository)
                .start(lancamentoStep)
                .build();
    }
}