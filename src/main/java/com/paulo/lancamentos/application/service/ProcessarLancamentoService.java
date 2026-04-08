package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import com.paulo.lancamentos.domain.ports.in.ProcessarLancamentoPort;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class ProcessarLancamentoService implements ProcessarLancamentoPort {

    private final LancamentoRepositoryPort repository;
    private final PublicarEventoPort eventPublisher;

    public ProcessarLancamentoService(
            LancamentoRepositoryPort repository,
            PublicarEventoPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Lancamento processar(
            String lancamentoId,
            String contaId,
            BigDecimal valor,
            TipoLancamento tipo,
            LocalDateTime dataHoraArquivo) {

        if (repository.existePorLancamentoId(lancamentoId)) {
            log.warn("Lançamento já processado: {}", lancamentoId);
            return repository.buscarPorLancamentoId(lancamentoId).orElseThrow();
        }

        try {
            Lancamento lancamento = new Lancamento(
                    null,
                    lancamentoId,
                    contaId,
                    valor,
                    tipo,
                    dataHoraArquivo,
                    LocalDateTime.now(),
                    StatusLancamento.PENDENTE,
                    null
            );
            Lancamento salvo = repository.salvar(lancamento);
            eventPublisher.publicarSolicitacaoStatus(contaId, salvo.getLancamentoId());
            return salvo;

        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detectada para lancamentoId: {}", lancamentoId);
            return repository.buscarPorLancamentoId(lancamentoId).orElseThrow();
        }
    }
}