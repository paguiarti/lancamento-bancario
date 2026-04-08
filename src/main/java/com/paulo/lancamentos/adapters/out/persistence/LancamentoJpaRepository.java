package com.paulo.lancamentos.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {
    Page<LancamentoEntity> findByContaId(String contaId, Pageable pageable);
    Optional<LancamentoEntity> findByLancamentoId(String lancamentoId);
    boolean existsByLancamentoId(String lancamentoId);
}