package com.paulo.lancamentos.adapters.in.batch;

import com.paulo.lancamentos.domain.model.Lancamento;
import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class LancamentoItemProcessor implements ItemProcessor<String[], Lancamento> {

    @Override
    public Lancamento process(String[] partes) {
        try {
            if (partes.length < 5) return null;

            String lancamentoId = partes[0].trim();
            String contaId = partes[1].trim();
            BigDecimal valor = new BigDecimal(partes[2].trim());
            TipoLancamento tipo = TipoLancamento.valueOf(partes[3].trim());
            LocalDateTime dataHoraArquivo = LocalDateTime.parse(partes[4].trim());

            return new Lancamento(
                    null,
                    lancamentoId,
                    contaId,
                    valor,
                    tipo,
                    dataHoraArquivo,
                    LocalDateTime.now(),
                    StatusLancamento.PENDENTE,
                    null
            );
        } catch (Exception e) {
            log.warn("Linha inválida, ignorando: {}", (Object) partes);
            return null;
        }
    }
}