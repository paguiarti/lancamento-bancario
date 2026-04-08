package com.paulo.lancamentos.adapters.in.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatusContaRecebidoEvent {
    private final String lancamentoId;
    private final String statusConta;
}