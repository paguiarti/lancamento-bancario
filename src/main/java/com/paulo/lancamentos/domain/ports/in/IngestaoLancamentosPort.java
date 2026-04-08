package com.paulo.lancamentos.domain.ports.in;

public interface IngestaoLancamentosPort {
    void processarArquivo(String caminhoArquivo);
}