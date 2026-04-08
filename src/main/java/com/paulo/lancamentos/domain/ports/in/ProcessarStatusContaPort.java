package com.paulo.lancamentos.domain.ports.in;

import com.paulo.lancamentos.domain.model.enums.StatusConta;

public interface ProcessarStatusContaPort {
    void processarStatus(String lancamentoId, StatusConta statusConta);
}