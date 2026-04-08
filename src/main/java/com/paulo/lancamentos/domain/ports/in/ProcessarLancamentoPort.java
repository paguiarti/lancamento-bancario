package com.paulo.lancamentos.domain.ports.in;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProcessarLancamentoPort {
    Lancamento processar(
            String lancamentoId,
            String contaId,
            BigDecimal valor,
            TipoLancamento tipo,
            LocalDateTime dataHoraArquivo);
}