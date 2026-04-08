package com.paulo.lancamentos.adapters.out.persistence;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LancamentoRepositoryAdapter implements LancamentoRepositoryPort {

    private final LancamentoJpaRepository jpaRepository;

    public LancamentoRepositoryAdapter(LancamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lancamento salvar(Lancamento lancamento) {
        LancamentoEntity salvo = jpaRepository.save(toEntity(lancamento));
        return toDomain(salvo);
    }

    @Override
    public List<Lancamento> salvarTodos(List<? extends Lancamento> lancamentos) {
        List<LancamentoEntity> entities = lancamentos.stream()
                .map(this::toEntity)
                .toList();
        return jpaRepository.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LancamentoEntity toEntity(Lancamento lancamento) {
        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(lancamento.getId());
        entity.setLancamentoId(lancamento.getLancamentoId());
        entity.setContaId(lancamento.getContaId());
        entity.setValor(lancamento.getValor());
        entity.setTipo(lancamento.getTipo());
        entity.setStatus(lancamento.getStatus());
        entity.setDataHoraArquivo(lancamento.getDataHoraArquivo());
        entity.setDataHoraCriacao(lancamento.getDataHoraCriacao());
        entity.setDataHoraProcessamento(lancamento.getDataHoraProcessamento());
        return entity;
    }

    @Override
    public Page<Lancamento> buscarPorContaId(String contaId, Pageable pageable) {
        return jpaRepository.findByContaId(contaId, pageable)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorLancamentoId(String lancamentoId) {
        return jpaRepository.existsByLancamentoId(lancamentoId);
    }

    @Override
    public Optional<Lancamento> buscarPorLancamentoId(String lancamentoId) {
        return jpaRepository.findByLancamentoId(lancamentoId)
                .map(this::toDomain);
    }

    private Lancamento toDomain(LancamentoEntity e) {
        return new Lancamento(
                e.getId(),
                e.getLancamentoId(),
                e.getContaId(),
                e.getValor(),
                e.getTipo(),
                e.getDataHoraArquivo(),
                e.getDataHoraCriacao(),
                e.getStatus(),
                e.getDataHoraProcessamento()
        );
    }
}