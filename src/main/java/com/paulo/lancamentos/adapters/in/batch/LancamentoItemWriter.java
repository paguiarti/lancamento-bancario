package com.paulo.lancamentos.adapters.in.batch;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.ports.out.LancamentoRepositoryPort;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LancamentoItemWriter implements ItemWriter<Lancamento> {

    private final LancamentoRepositoryPort repository;
    private final PublicarEventoPort eventPublisher;

    public LancamentoItemWriter(LancamentoRepositoryPort repository,
                                PublicarEventoPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void write(Chunk<? extends Lancamento> chunk) {
        List<? extends Lancamento> lancamentos = chunk.getItems();

        List<Lancamento> salvos = repository.salvarTodos(lancamentos);

        salvos.forEach(l -> eventPublisher.publicarSolicitacaoStatus(
                l.getContaId(),
                l.getLancamentoId()
        ));

        log.info("Chunk processado: {} lançamentos", lancamentos.size());
    }
}