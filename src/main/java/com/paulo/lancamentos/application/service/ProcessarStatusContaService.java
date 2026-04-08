package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusConta;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.ports.in.ProcessarStatusContaPort;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class ProcessarStatusContaService implements ProcessarStatusContaPort {

    private final LancamentoRepositoryPort repository;
    private final PublicarEventoPort eventPublisher;

    public ProcessarStatusContaService(
            LancamentoRepositoryPort repository,
            PublicarEventoPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processarStatus(String lancamentoId, StatusConta statusConta) {
        Optional<Lancamento> optional = repository.buscarPorLancamentoId(lancamentoId);

        if (optional.isEmpty()) {
            log.warn("Lançamento não encontrado: {}", lancamentoId);
            return;
        }

        Lancamento lancamento = optional.get();
        lancamento.setDataHoraProcessamento(LocalDateTime.now());

        if (StatusConta.ATIVA.equals(statusConta)) {
            lancamento.setStatus(StatusLancamento.PROCESSADO);
            eventPublisher.publicarLancamentoContabil(
                    lancamento.getLancamentoId(),
                    lancamento.getContaId(),
                    lancamento.getValor(),
                    lancamento.getTipo(),
                    lancamento.getDataHoraArquivo()
            );
        } else {
            lancamento.setStatus(StatusLancamento.RECUSADO);
        }

        repository.salvar(lancamento);
    }
}