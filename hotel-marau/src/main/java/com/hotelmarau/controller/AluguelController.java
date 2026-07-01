package com.hotelmarau.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelmarau.dto.AluguelDTO;
import com.hotelmarau.exception.CapacidadeExcedidaException;
import com.hotelmarau.exception.DataInvalidaException;
import com.hotelmarau.exception.QuartoIndisponivelException;
import com.hotelmarau.exception.RecursoNaoPermitidoException;
import com.hotelmarau.model.Aluguel;
import com.hotelmarau.service.AluguelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/alugueis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AluguelController {

    private final AluguelService aluguelService;

    @GetMapping
    public ResponseEntity<List<Aluguel>> listarTodos() {
        return ResponseEntity.ok(aluguelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.buscarPorId(id));
    }

    @GetMapping("/residencia/{residenciaId}")
    public ResponseEntity<List<Aluguel>> listarPorResidencia(@PathVariable Long residenciaId) {
        return ResponseEntity.ok(aluguelService.listarPorResidencia(residenciaId));
    }

    @PostMapping
    public ResponseEntity<Aluguel> criar(@Valid @RequestBody AluguelDTO dto)
            throws DataInvalidaException, QuartoIndisponivelException,
            CapacidadeExcedidaException, RecursoNaoPermitidoException {
        return ResponseEntity.status(HttpStatus.CREATED).body(aluguelService.criar(dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Aluguel> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.cancelar(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Aluguel> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.concluir(id));
    }

    // Retorna o recibo formatado do aluguel
    @GetMapping("/{id}/recibo")
    public ResponseEntity<String> gerarRecibo(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.gerarRecibo(id));
    }
}
