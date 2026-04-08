package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarLancamentoServiceTest {

    @Mock
    private LancamentoRepositoryPort repository;

    @Mock
    private PublicarEventoPort eventPublisher;

    private ProcessarLancamentoService service;

    @BeforeEach
    void setUp() {
        service = new ProcessarLancamentoService(repository, eventPublisher);
    }

    @Test
    void devePersistirLancamentoComStatusPendente() {
        String lancamentoId = "uuid-001";
        String contaId = "123";
        BigDecimal valor = new BigDecimal("100.00");
        TipoLancamento tipo = TipoLancamento.DEBITO;
        LocalDateTime dataHora = LocalDateTime.now();

        Lancamento lancamentoSalvo = new Lancamento(
                1L, lancamentoId, contaId, valor, tipo,
                dataHora, LocalDateTime.now(), StatusLancamento.PENDENTE, null
        );

        when(repository.existePorLancamentoId(lancamentoId)).thenReturn(false);
        when(repository.salvar(any())).thenReturn(lancamentoSalvo);

        Lancamento resultado = service.processar(lancamentoId, contaId, valor, tipo, dataHora);

        assertEquals(StatusLancamento.PENDENTE, resultado.getStatus());
        verify(repository).salvar(any());
    }

    @Test
    void devePublicarEventoAposPeristir() {
        String lancamentoId = "uuid-001";
        String contaId = "123";
        BigDecimal valor = new BigDecimal("100.00");
        LocalDateTime dataHora = LocalDateTime.now();

        Lancamento lancamentoSalvo = new Lancamento(
                1L, lancamentoId, contaId, valor, TipoLancamento.DEBITO,
                dataHora, LocalDateTime.now(), StatusLancamento.PENDENTE, null
        );

        when(repository.existePorLancamentoId(lancamentoId)).thenReturn(false);
        when(repository.salvar(any())).thenReturn(lancamentoSalvo);

        service.processar(lancamentoId, contaId, valor, TipoLancamento.DEBITO, dataHora);

        verify(eventPublisher).publicarSolicitacaoStatus(contaId, lancamentoId);
    }

    @Test
    void deveIgnorarLancamentoDuplicado() {
        String lancamentoId = "uuid-001";
        String contaId = "123";
        BigDecimal valor = new BigDecimal("100.00");
        LocalDateTime dataHora = LocalDateTime.now();

        Lancamento lancamentoExistente = new Lancamento(
                1L, lancamentoId, contaId, valor, TipoLancamento.DEBITO,
                dataHora, LocalDateTime.now(), StatusLancamento.PENDENTE, null
        );

        when(repository.existePorLancamentoId(lancamentoId)).thenReturn(true);
        when(repository.buscarPorLancamentoId(lancamentoId))
                .thenReturn(java.util.Optional.of(lancamentoExistente));

        service.processar(lancamentoId, contaId, valor, TipoLancamento.DEBITO, dataHora);

        verify(repository, never()).salvar(any());
        verify(eventPublisher, never()).publicarSolicitacaoStatus(any(), any());
    }
}