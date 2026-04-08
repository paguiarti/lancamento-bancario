package com.paulo.lancamentos.adapters.out.messaging.dto;

import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LancamentoContabilEvent {
    private String lancamentoId;
    private String contaId;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private LocalDateTime dataHoraLancamento;
}
