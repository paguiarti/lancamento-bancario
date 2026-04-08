package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusConta;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarStatusContaServiceTest {

    @Mock
    private LancamentoRepositoryPort repository;

    @Mock
    private PublicarEventoPort eventPublisher;

    private ProcessarStatusContaService service;

    @BeforeEach
    void setUp() {
        service = new ProcessarStatusContaService(repository, eventPublisher);
    }

    @Test
    void deveProcessarLancamentoQuandoContaAtiva() {
        Lancamento lancamento = lancamentoPendente();
        when(repository.buscarPorLancamentoId("uuid-001")).thenReturn(Optional.of(lancamento));

        service.processarStatus("uuid-001", StatusConta.ATIVA);

        assertEquals(StatusLancamento.PROCESSADO, lancamento.getStatus());
        assertNotNull(lancamento.getDataHoraProcessamento());
        verify(repository).salvar(lancamento);
        verify(eventPublisher).publicarLancamentoContabil(
                lancamento.getLancamentoId(),
                lancamento.getContaId(),
                lancamento.getValor(),
                lancamento.getTipo(),
                lancamento.getDataHoraArquivo()
        );
    }

    @Test
    void deveRecusarLancamentoQuandoContaCancelada() {
        Lancamento lancamento = lancamentoPendente();
        when(repository.buscarPorLancamentoId("uuid-001")).thenReturn(Optional.of(lancamento));

        service.processarStatus("uuid-001", StatusConta.CANCELADA);

        assertEquals(StatusLancamento.RECUSADO, lancamento.getStatus());
        assertNotNull(lancamento.getDataHoraProcessamento());
        verify(repository).salvar(lancamento);
        verify(eventPublisher, never()).publicarLancamentoContabil(any(), any(), any(), any(), any());
    }

    @Test
    void deveRecusarLancamentoQuandoContaBloqueioJudicial() {
        Lancamento lancamento = lancamentoPendente();
        when(repository.buscarPorLancamentoId("uuid-001")).thenReturn(Optional.of(lancamento));

        service.processarStatus("uuid-001", StatusConta.BLOQUEIO_JUDICIAL);

        assertEquals(StatusLancamento.RECUSADO, lancamento.getStatus());
        verify(repository).salvar(lancamento);
        verify(eventPublisher, never()).publicarLancamentoContabil(any(), any(), any(), any(), any());
    }

    @Test
    void deveIgnorarQuandoLancamentoNaoEncontrado() {
        when(repository.buscarPorLancamentoId("uuid-inexistente")).thenReturn(Optional.empty());

        service.processarStatus("uuid-inexistente", StatusConta.ATIVA);

        verify(repository, never()).salvar(any());
        verify(eventPublisher, never()).publicarLancamentoContabil(any(), any(), any(), any(), any());
    }

    private Lancamento lancamentoPendente() {
        return new Lancamento(
                1L, "uuid-001", "123",
                new BigDecimal("100.00"),
                TipoLancamento.DEBITO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusLancamento.PENDENTE,
                null
        );
    }
}