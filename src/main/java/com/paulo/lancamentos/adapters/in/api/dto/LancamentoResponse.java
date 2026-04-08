package com.paulo.lancamentos.adapters.in.api.dto;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class LancamentoResponse {
    private final Long id;
    private final String lancamentoId;
    private final String contaId;
    private final BigDecimal valor;
    private final TipoLancamento tipo;
    private final StatusLancamento status;
    private final LocalDateTime dataHoraArquivo;
    private final LocalDateTime dataHoraCriacao;
    private final LocalDateTime dataHoraProcessamento;

    public LancamentoResponse(Lancamento lancamento) {
        this.id = lancamento.getId();
        this.lancamentoId = lancamento.getLancamentoId();
        this.contaId = lancamento.getContaId();
        this.valor = lancamento.getValor();
        this.tipo = lancamento.getTipo();
        this.status = lancamento.getStatus();
        this.dataHoraArquivo = lancamento.getDataHoraArquivo();
        this.dataHoraCriacao = lancamento.getDataHoraCriacao();
        this.dataHoraProcessamento = lancamento.getDataHoraProcessamento();
    }
}