package com.paulo.lancamentos.application.service;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.in.ListarLancamentosPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class ListarLancamentosService implements ListarLancamentosPort {

    private final LancamentoRepositoryPort repository;

    public ListarLancamentosService(LancamentoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Lancamento> buscarPorLancamentoId(String lancamentoId) {
        return repository.buscarPorLancamentoId(lancamentoId);
    }

    @Override
    public Page<Lancamento> buscarPorContaId(String contaId, Pageable pageable) {
        return repository.buscarPorContaId(contaId, pageable);
    }
}
