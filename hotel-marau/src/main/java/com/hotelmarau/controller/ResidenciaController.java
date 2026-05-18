package com.hotelmarau.controller;

import com.hotelmarau.dto.ResidenciaDetalhesDTO;
import com.hotelmarau.dto.ResidenciaDTO;

import com.hotelmarau.service.ResidenciaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResidenciaController {

    private final ResidenciaService residenciaService;

    @GetMapping
    public ResponseEntity<List<ResidenciaDetalhesDTO>> listarTodas() {
        return ResponseEntity.ok(residenciaService.listarTodasDetalhes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidenciaDetalhesDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(residenciaService.buscarPorIdDetalhes(id));
    }

    @PostMapping
    public ResponseEntity<ResidenciaDetalhesDTO> criar(@Valid @RequestBody ResidenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(residenciaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResidenciaDetalhesDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ResidenciaDTO dto) {
        return ResponseEntity.ok(residenciaService.atualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        residenciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
