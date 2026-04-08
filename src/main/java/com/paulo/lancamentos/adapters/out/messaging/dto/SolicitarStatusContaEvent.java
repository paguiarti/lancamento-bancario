package com.paulo.lancamentos.adapters.out.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SolicitarStatusContaEvent {
    private final String contaId;
    private final String lancamentoId;
}