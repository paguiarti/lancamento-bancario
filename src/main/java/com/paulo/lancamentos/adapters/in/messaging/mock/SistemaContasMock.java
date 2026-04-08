package com.paulo.lancamentos.adapters.in.messaging.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulo.lancamentos.adapters.out.messaging.dto.SolicitarStatusContaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Profile("dev")
public class SistemaContasMock {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> STATUS_CONTAS = Map.of(
            "123", "ATIVA",
            "456", "ATIVA",
            "789", "ATIVA",
            "999", "CANCELADA",
            "111", "ATIVA",
            "222", "BLOQUEIO_JUDICIAL",
            "333", "ATIVA",
            "444", "ATIVA",
            "555", "ATIVA",
            "666", "ATIVA"
    );

    public SistemaContasMock(KafkaTemplate<String, byte[]> kafkaTemplate,
                             ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "solicitar-status-conta", groupId = "sistema-contas-mock")
    public void handle(ConsumerRecord<String, byte[]> record) {
        try {
            SolicitarStatusContaEvent evento = objectMapper.readValue(record.value(), SolicitarStatusContaEvent.class);

            String status = STATUS_CONTAS.getOrDefault(evento.getContaId(), "ATIVA");

            Map<String, String> resposta = Map.of(
                    "lancamentoId", evento.getLancamentoId(),
                    "statusConta", status
            );

            kafkaTemplate.send("status-conta-resposta", objectMapper.writeValueAsBytes(resposta));
            log.info("Mock respondeu contaId: {} com status: {}", evento.getContaId(), status);

        } catch (Exception e) {
            log.error("Erro no mock Sistema de Contas", e);
        }
    }
}