package com.paulo.lancamentos.domain.ports.out;

import com.paulo.lancamentos.domain.model.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LancamentoRepositoryPort {
    Lancamento salvar(Lancamento lancamento);
    Optional<Lancamento> buscarPorLancamentoId(String lancamentoId);
    boolean existePorLancamentoId(String lancamentoId);
    Page<Lancamento> buscarPorContaId(String contaId, Pageable pageable);
    List<Lancamento> salvarTodos(List<? extends Lancamento> lancamentos);
}