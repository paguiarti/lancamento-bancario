package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarLancamentosServiceTest {

    @Mock
    private LancamentoRepositoryPort repository;

    private ListarLancamentosService service;

    @BeforeEach
    void setUp() {
        service = new ListarLancamentosService(repository);
    }

    @Test
    void deveRetornarLancamentoQuandoEncontrado() {
        Lancamento lancamento = lancamento("uuid-001", "123");
        when(repository.buscarPorLancamentoId("uuid-001")).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.buscarPorLancamentoId("uuid-001");

        assertTrue(resultado.isPresent());
        assertEquals("uuid-001", resultado.get().getLancamentoId());
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrado() {
        when(repository.buscarPorLancamentoId("uuid-inexistente")).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.buscarPorLancamentoId("uuid-inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarLancamentosDaConta() {
        List<Lancamento> lancamentos = List.of(
                lancamento("uuid-001", "123"),
                lancamento("uuid-002", "123")
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<Lancamento> page = new PageImpl<>(lancamentos);

        when(repository.buscarPorContaId("123", pageable)).thenReturn(page);

        Page<Lancamento> resultado = service.buscarPorContaId("123", pageable);

        assertEquals(2, resultado.getContent().size());
        verify(repository).buscarPorContaId("123", pageable);
    }

    @Test
    void deveRetornarListaVaziaQuandoContaSemLancamentos() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Lancamento> pageVazia = new PageImpl<>(List.of());

        when(repository.buscarPorContaId("999", pageable)).thenReturn(pageVazia);

        Page<Lancamento> resultado = service.buscarPorContaId("999", pageable);

        assertTrue(resultado.getContent().isEmpty());
    }

    private Lancamento lancamento(String lancamentoId, String contaId) {
        return new Lancamento(
                1L, lancamentoId, contaId,
                new BigDecimal("100.00"),
                TipoLancamento.DEBITO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusLancamento.PENDENTE,
                null
        );
    }
}