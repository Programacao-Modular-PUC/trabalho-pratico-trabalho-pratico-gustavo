package com.hotelmarau.controller;

import com.hotelmarau.dto.ClienteDTO;
import com.hotelmarau.model.Aluguel;
import com.hotelmarau.model.Cliente;
import com.hotelmarau.service.AluguelService;
import com.hotelmarau.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;
    private final AluguelService aluguelService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<Aluguel>> listarReservas(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.listarPorCliente(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtém o histórico completo de hospedagem de um cliente
     */
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<Aluguel>> obterHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obterHistoricoCliente(id));
    }

    /**
     * Obtém apenas os aluguéis ativos de um cliente
     */
    @GetMapping("/{id}/alugueis/ativos")
    public ResponseEntity<List<Aluguel>> obterAlugueisAtivos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obterAlugueisAtivos(id));
    }

    /**
     * Obtém apenas os aluguéis concluídos de um cliente
     */
    @GetMapping("/{id}/alugueis/concluidos")
    public ResponseEntity<List<Aluguel>> obterAluguelsConcluidos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obterAluguelsConcluidos(id));
    }

    /**
     * Obtém apenas os aluguéis cancelados de um cliente
     */
    @GetMapping("/{id}/alugueis/cancelados")
    public ResponseEntity<List<Aluguel>> obterAluguelsCancelados(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obterAluguelsCancelados(id));
    }

    /**
     * Gera um relatório formatado do histórico do cliente
     */
    @GetMapping("/{id}/historico/relatorio")
    public ResponseEntity<String> gerarRelatoriHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.gerarRelatoriHistorico(id));
    }
}
