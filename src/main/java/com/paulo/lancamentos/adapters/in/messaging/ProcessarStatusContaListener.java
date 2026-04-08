package com.paulo.lancamentos.adapters.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulo.lancamentos.adapters.in.messaging.dto.StatusContaRecebidoEvent;
import com.paulo.lancamentos.domain.model.enums.StatusConta;
import com.paulo.lancamentos.domain.ports.in.ProcessarStatusContaPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessarStatusContaListener {

    private final ProcessarStatusContaPort processarStatusContaPort;
    private final ObjectMapper objectMapper;

    public ProcessarStatusContaListener(ProcessarStatusContaPort processarStatusContaPort,
                                        ObjectMapper objectMapper) {
        this.processarStatusContaPort = processarStatusContaPort;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "status-conta-resposta", groupId = "lancamentos-group")
    public void handle(ConsumerRecord<String, byte[]> record) {
        try {
            StatusContaRecebidoEvent event = objectMapper.readValue(record.value(), StatusContaRecebidoEvent.class);
            StatusConta statusConta = StatusConta.valueOf(event.getStatusConta());
            processarStatusContaPort.processarStatus(event.getLancamentoId(), statusConta);
        } catch (IllegalArgumentException e) {
            log.error("Status inválido recebido: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar status da conta", e);
        }
    }
}