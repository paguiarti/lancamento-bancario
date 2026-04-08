package com.paulo.lancamentos.application.config;

import com.paulo.lancamentos.application.service.IngestaoLancamentosService;
import com.paulo.lancamentos.application.service.ListarLancamentosService;
import com.paulo.lancamentos.application.service.ProcessarLancamentoService;
import com.paulo.lancamentos.application.service.ProcessarStatusContaService;
import com.paulo.lancamentos.domain.ports.in.IngestaoLancamentosPort;
import com.paulo.lancamentos.domain.ports.in.ListarLancamentosPort;
import com.paulo.lancamentos.domain.ports.in.ProcessarLancamentoPort;
import com.paulo.lancamentos.domain.ports.in.ProcessarStatusContaPort;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ProcessarLancamentoPort processarLancamentoPort(
            LancamentoRepositoryPort repository,
            PublicarEventoPort eventPublisher) {
        return new ProcessarLancamentoService(repository, eventPublisher);
    }

    @Bean
    public ListarLancamentosPort listarLancamentosPort(
            LancamentoRepositoryPort repository) {
        return new ListarLancamentosService(repository);
    }

    @Bean
    public ProcessarStatusContaPort processarStatusContaPort(
            LancamentoRepositoryPort repository,
            PublicarEventoPort eventPublisher) {
        return new ProcessarStatusContaService(repository, eventPublisher);
    }

    @Bean
    public IngestaoLancamentosPort ingestaoLancamentosPort(
            JobOperator jobOperator,
            Job lancamentoJob,
            @Value("${ingestao.arquivo.caminho}") String caminhoArquivo) {
        return new IngestaoLancamentosService(jobOperator, lancamentoJob, caminhoArquivo);
    }
}