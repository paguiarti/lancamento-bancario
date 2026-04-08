package com.paulo.lancamentos.adapters.in.api;

import com.paulo.lancamentos.adapters.in.api.dto.LancamentoResponse;
import com.paulo.lancamentos.domain.ports.in.IngestaoLancamentosPort;
import com.paulo.lancamentos.domain.ports.in.ListarLancamentosPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final ListarLancamentosPort listarLancamentosPort;
    private final IngestaoLancamentosPort ingestaoLancamentosPort;

    public LancamentoController(
            ListarLancamentosPort listarLancamentosPort,
            IngestaoLancamentosPort ingestaoLancamentosPort) {
        this.listarLancamentosPort = listarLancamentosPort;
        this.ingestaoLancamentosPort = ingestaoLancamentosPort;
    }

    @GetMapping("/{lancamentoId}")
    public ResponseEntity<LancamentoResponse> buscarPorLancamentoId(@PathVariable String lancamentoId) {
        return listarLancamentosPort.buscarPorLancamentoId(lancamentoId)
                .map(LancamentoResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<LancamentoResponse>> buscarPorContaId(
            @RequestParam String contaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<LancamentoResponse> paginaLancamentos = listarLancamentosPort
                .buscarPorContaId(contaId, pageable)
                .map(LancamentoResponse::new);

        return ResponseEntity.ok(paginaLancamentos);
    }

    @PostMapping("/arquivo")
    public ResponseEntity<String> processarArquivo(@RequestParam String caminho) {
        log.info("Processamento manual iniciado para arquivo: {}", caminho);
        ingestaoLancamentosPort.processarArquivo(caminho);
        return ResponseEntity.accepted().body("Processamento iniciado");
    }
}