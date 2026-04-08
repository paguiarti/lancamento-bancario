package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.ports.in.IngestaoLancamentosPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class IngestaoLancamentosService implements IngestaoLancamentosPort {

    private final JobOperator jobOperator;
    private final Job lancamentoJob;
    private final String caminhoArquivoPadrao;

    public IngestaoLancamentosService(JobOperator jobOperator,
                                      Job lancamentoJob,
                                      String caminhoArquivoPadrao) {
        this.jobOperator = jobOperator;
        this.lancamentoJob = lancamentoJob;
        this.caminhoArquivoPadrao = caminhoArquivoPadrao;
    }

    @Async
    @Override
    public void processarArquivo(String caminhoArquivo) {
        try {
            log.info("Iniciando processamento do arquivo: {}", caminhoArquivo);
            long inicio = System.currentTimeMillis();

            JobParameters params = new JobParametersBuilder()
                    .addString("caminho", caminhoArquivo)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobOperator.start(lancamentoJob, params);

            long fim = System.currentTimeMillis();
            log.info("Processamento finalizado! Tempo: {}ms", (fim - inicio));

        } catch (Exception e) {
            log.error("Erro ao iniciar job de ingestão", e);
            throw new RuntimeException("Erro ao processar arquivo", e);
        }
    }
}