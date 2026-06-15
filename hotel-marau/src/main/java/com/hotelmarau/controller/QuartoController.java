package com.hotelmarau.controller;

import com.hotelmarau.dto.QuartoDTO;
import com.hotelmarau.exception.DataInvalidaException;
import com.hotelmarau.model.Quarto;
import com.hotelmarau.service.QuartoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/quartos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuartoController {

    private final QuartoService quartoService;

    // Lista todos os quartos de uma residência
    @GetMapping("/residencia/{residenciaId}")
    public ResponseEntity<List<Quarto>> listarPorResidencia(@PathVariable Long residenciaId) {
        return ResponseEntity.ok(quartoService.listarPorResidencia(residenciaId));
    }

    // Lista quartos ativos de uma residência
    @GetMapping("/residencia/{residenciaId}/ativos")
    public ResponseEntity<List<Quarto>> listarAtivos(@PathVariable Long residenciaId) {
        return ResponseEntity.ok(quartoService.listarAtivos(residenciaId));
    }

    // Lista quartos disponíveis em um período
    @GetMapping("/residencia/{residenciaId}/disponiveis")
    public ResponseEntity<List<Quarto>> listarDisponiveis(
            @PathVariable Long residenciaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataSaida) {
        return ResponseEntity.ok(quartoService.listarDisponiveis(residenciaId, dataEntrada, dataSaida));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(quartoService.buscarPorId(id));
    }

    // Cria quarto em uma residência (tipo definido no body: INDIVIDUAL, DUPLO ou FAMILIA)
    @PostMapping("/residencia/{residenciaId}")
    public ResponseEntity<Quarto> criar(@PathVariable Long residenciaId,
                                        @Valid @RequestBody QuartoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quartoService.criar(residenciaId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @Valid @RequestBody QuartoDTO dto) {
        return ResponseEntity.ok(quartoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        quartoService.ativar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        quartoService.desativar(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Filtra quartos de uma residência por tipo
     * @param residenciaId ID da residência
     * @param tipo Tipo do quarto: INDIVIDUAL, DUPLO ou FAMILIA
     */
    @GetMapping("/residencia/{residenciaId}/tipo/{tipo}")
    public ResponseEntity<?> filtrarPorTipo(@PathVariable Long residenciaId, @PathVariable String tipo) {
        try {
            List<Quarto> quartos = quartoService.filtrarPorTipo(residenciaId, tipo);
            return ResponseEntity.ok(quartos);
        } catch (DataInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

    /**
     * Filtra quartos ativos de uma residência por tipo
     */
    @GetMapping("/residencia/{residenciaId}/tipo/{tipo}/ativos")
    public ResponseEntity<?> filtrarPorTipoAtivos(@PathVariable Long residenciaId, @PathVariable String tipo) {
        try {
            List<Quarto> quartos = quartoService.filtrarPorTipoAtivos(residenciaId, tipo);
            return ResponseEntity.ok(quartos);
        } catch (DataInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

    /**
     * Filtra quartos disponíveis de uma residência por tipo e período
     */
    @GetMapping("/residencia/{residenciaId}/tipo/{tipo}/disponiveis")
    public ResponseEntity<?> filtrarPorTipoDisponiveis(
            @PathVariable Long residenciaId,
            @PathVariable String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataSaida) {
        try {
            List<Quarto> quartos = quartoService.filtrarPorTipoDisponiveis(residenciaId, tipo, dataEntrada, dataSaida);
            return ResponseEntity.ok(quartos);
        } catch (DataInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }
}
