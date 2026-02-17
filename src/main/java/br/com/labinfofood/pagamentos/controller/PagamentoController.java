package br.com.labinfofood.pagamentos.controller;

import br.com.labinfofood.pagamentos.dto.PagamentoDto;
import br.com.labinfofood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;


    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public Page<PagamentoDto> listarPagamentos(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return pagamentoService.obterTodos(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> detalharPagamento(@PathVariable @NotNull  Long id) {
        try {
            PagamentoDto pagamentoDto = pagamentoService.obterPorId(id);
            return ResponseEntity.ok(pagamentoDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PagamentoDto> criarPagamento(@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriBuilder) {
        PagamentoDto pagamentoCriado = pagamentoService.criarPagamento(pagamentoDto);
        var uri = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamentoCriado.getId()).toUri();
        return ResponseEntity.created(uri).body(pagamentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDto> atualizarPagamento(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDto pagamentoDto) {
        try {
            PagamentoDto pagamentoAtualizado = pagamentoService.atualizarPagamento(id, pagamentoDto);
            return ResponseEntity.ok(pagamentoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPagamento(@PathVariable @NotNull Long id) {
        try {
            pagamentoService.deletarPagamento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alteraStatus(id);
    }
}
