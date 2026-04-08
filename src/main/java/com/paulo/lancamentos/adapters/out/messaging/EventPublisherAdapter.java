package com.paulo.lancamentos.adapters.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulo.lancamentos.adapters.out.messaging.dto.LancamentoContabilEvent;
import com.paulo.lancamentos.adapters.out.messaging.dto.SolicitarStatusContaEvent;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import com.paulo.lancamentos.domain.ports.out.PublicarEventoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class EventPublisherAdapter implements PublicarEventoPort {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisherAdapter(KafkaTemplate<String, byte[]> kafkaTemplate,
                                 ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicarSolicitacaoStatus(String contaId, String lancamentoId) {
        try {
            SolicitarStatusContaEvent evento = new SolicitarStatusContaEvent(contaId, lancamentoId);
            kafkaTemplate.send("solicitar-status-conta", objectMapper.writeValueAsBytes(evento));
            log.info("Evento solicitacao status publicado para contaId: {}", contaId);
        } catch (Exception e) {
            log.error("Erro ao publicar solicitacao status para contaId: {}", contaId, e);
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }

    @Override
    public void publicarLancamentoContabil(
            String lancamentoId,
            String contaId,
            BigDecimal valor,
            TipoLancamento tipo,
            LocalDateTime dataHoraLancamento) {
        try {
            LancamentoContabilEvent evento = new LancamentoContabilEvent(lancamentoId, contaId, valor, tipo, dataHoraLancamento);
            kafkaTemplate.send("lancamento-contabil", objectMapper.writeValueAsBytes(evento));
            log.info("Evento lancamento contabil publicado para lancamentoId: {}", lancamentoId);
        } catch (Exception e) {
            log.error("Erro ao publicar lancamento contabil para lancamentoId: {}", lancamentoId, e);
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }
}