package com.hotelmarau.service;

import com.hotelmarau.dto.QuartoDTO;
import com.hotelmarau.model.*;
import com.hotelmarau.repository.QuartoRepository;
import com.hotelmarau.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public List<Quarto> listarAtivos(Long residenciaId) {
        return quartoRepository.findByResidenciaIdAndAtivoTrue(residenciaId);
    }

    public List<Quarto> listarDisponiveis(Long residenciaId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return quartoRepository.findQuartosDisponiveis(residenciaId, dataInicio, dataFim);
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com id: " + id));
    }

    @Transactional
    public Quarto criar(Long residenciaId, QuartoDTO dto) {
        Residencia residencia = residenciaRepository.findById(residenciaId)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada com id: " + residenciaId));

        Quarto quarto = construirQuarto(dto);
        quarto.setResidencia(residencia);
        return quartoRepository.save(quarto);
    }

    @Transactional
    public Quarto atualizar(Long id, QuartoDTO dto) {
        Quarto existente = buscarPorId(id);
        existente.setValorBase(dto.getValorBase());
        existente.setPossuiAr(dto.isPossuiAr());
        existente.setPossuiHidro(dto.isPossuiHidro());

        // Atualiza campos específicos por tipo
        if (existente instanceof QuartoIndividual qi && dto.getNumeroCamas() != null) {
            qi.setNumeroCamas(dto.getNumeroCamas());
        } else if (existente instanceof QuartoDuplo qd) {
            if (dto.getTipoCama() != null) qd.setTipoCama(dto.getTipoCama());
            qd.setTemBerco(dto.isTemBerco());
        } else if (existente instanceof QuartoFamilia qf) {
            if (dto.getCamasSolteiro() != null) qf.setCamasSolteiro(dto.getCamasSolteiro());
            if (dto.getCamasCasal() != null) qf.setCamasCasal(dto.getCamasCasal());
            if (dto.getCamasQueenKing() != null) qf.setCamasQueenKing(dto.getCamasQueenKing());
            if (dto.getNumeroAmbientes() != null) qf.setNumeroAmbientes(dto.getNumeroAmbientes());
        }

        return quartoRepository.save(existente);
    }

    @Transactional
    public void ativar(Long id) {
        Quarto q = buscarPorId(id);
        q.ativar();
        quartoRepository.save(q);
    }

    @Transactional
    public void desativar(Long id) {
        Quarto q = buscarPorId(id);
        q.desativar();
        quartoRepository.save(q);
    }

    private Quarto construirQuarto(QuartoDTO dto) {
        return switch (dto.getTipo().toUpperCase()) {
            case "INDIVIDUAL" -> {
                if (dto.getNumeroCamas() == null || dto.getNumeroCamas() < 1)
                    throw new IllegalArgumentException("Quarto Individual requer ao menos 1 cama.");
                yield new QuartoIndividual(dto.getValorBase(), dto.isPossuiAr(), dto.isPossuiHidro(), dto.getNumeroCamas());
            }
            case "DUPLO" -> {
                if (dto.getTipoCama() == null)
                    throw new IllegalArgumentException("Quarto Duplo requer o tipo de cama (CASAL_COMUM, QUEEN ou KING).");
                yield new QuartoDuplo(dto.getValorBase(), dto.isPossuiAr(), dto.isPossuiHidro(), dto.getTipoCama(), dto.isTemBerco());
            }
            case "FAMILIA" -> {
                int solteiro = dto.getCamasSolteiro() != null ? dto.getCamasSolteiro() : 0;
                int casal = dto.getCamasCasal() != null ? dto.getCamasCasal() : 0;
                int queenKing = dto.getCamasQueenKing() != null ? dto.getCamasQueenKing() : 0;
                int ambientes = dto.getNumeroAmbientes() != null ? dto.getNumeroAmbientes() : 1;
                if (solteiro + casal + queenKing < 1)
                    throw new IllegalArgumentException("Quarto Família requer ao menos 1 cama.");
                yield new QuartoFamilia(dto.getValorBase(), dto.isPossuiAr(), dto.isPossuiHidro(), solteiro, casal, queenKing, ambientes);
            }
            default -> throw new IllegalArgumentException("Tipo de quarto inválido: " + dto.getTipo() + ". Use INDIVIDUAL, DUPLO ou FAMILIA.");
        };
    }
}
