package com.paulo.lancamentos.domain.model;

import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Lancamento {
    private Long id;
    private String lancamentoId;
    private String contaId;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private LocalDateTime dataHoraArquivo;
    private LocalDateTime dataHoraCriacao;

    @Setter
    private StatusLancamento status;

    @Setter
    private LocalDateTime dataHoraProcessamento;
}