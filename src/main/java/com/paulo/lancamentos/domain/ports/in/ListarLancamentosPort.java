package com.paulo.lancamentos.domain.ports.in;

import com.paulo.lancamentos.domain.model.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ListarLancamentosPort {
    Optional<Lancamento> buscarPorLancamentoId(String id);
    Page<Lancamento> buscarPorContaId(String contaId, Pageable pageable);
}