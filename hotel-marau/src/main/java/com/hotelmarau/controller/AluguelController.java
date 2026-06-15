package com.hotelmarau.controller;

import com.hotelmarau.dto.AluguelDTO;
import com.hotelmarau.exception.*;
import com.hotelmarau.model.Aluguel;
import com.hotelmarau.service.AluguelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> criar(@Valid @RequestBody AluguelDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(aluguelService.criar(dto));
        } catch (DataInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de data: " + e.getMessage());
        } catch (QuartoIndisponivelException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Quarto indisponível: " + e.getMessage());
        } catch (CapacidadeExcedidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Capacidade excedida: " + e.getMessage());
        } catch (RecursoNaoPermitidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recurso não permitido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar aluguel: " + e.getMessage());
        }
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
