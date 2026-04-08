package com.paulo.lancamentos.application.scheduler;

import com.paulo.lancamentos.domain.ports.in.IngestaoLancamentosPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AgendadorProcessamento {
    private final IngestaoLancamentosPort ingestaoLancamentosPort;

    @Value("${ingestao.arquivo.caminho}")
    private String caminhoArquivo;

    public AgendadorProcessamento(IngestaoLancamentosPort ingestaoLancamentosPort) {
        this.ingestaoLancamentosPort = ingestaoLancamentosPort;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void executar() {
        log.info("Iniciando processamento agendado às 04:00");
        ingestaoLancamentosPort.processarArquivo(caminhoArquivo);
    }
}