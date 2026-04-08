package com.paulo.lancamentos.adapters.out.persistence;

import com.paulo.lancamentos.domain.model.enums.StatusLancamento;
import com.paulo.lancamentos.domain.model.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lancamentos", uniqueConstraints = {
        @UniqueConstraint(columnNames = "lancamento_id")
}, indexes = {
        @Index(name = "idx_lancamento_id", columnList = "lancamento_id")
})
public class LancamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lancamento_id", nullable = false, unique = true)
    private String lancamentoId;

    private String contaId;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    @Enumerated(EnumType.STRING)
    private StatusLancamento status;

    private LocalDateTime dataHoraArquivo;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraProcessamento;
}