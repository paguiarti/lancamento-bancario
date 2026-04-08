package com.paulo.lancamentos.domain.ports.out;

import com.paulo.lancamentos.domain.model.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PublicarEventoPort {
    void publicarSolicitacaoStatus(String contaId, String lancamentoId);
    void publicarLancamentoContabil(
            String lancamentoId,
            String contaId,
            BigDecimal valor,
            TipoLancamento tipo,
            LocalDateTime dataHoraLancamento);
}
