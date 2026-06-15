package com.hotelmarau.service;

import com.hotelmarau.dto.QuartoDTO;
import com.hotelmarau.exception.DataInvalidaException;
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

    /**
     * Filtra quartos de uma residência por tipo
     * @param residenciaId ID da residência
     * @param tipo Tipo do quarto: INDIVIDUAL, DUPLO ou FAMILIA
     * @return Lista de quartos do tipo especificado
     */
    public List<Quarto> filtrarPorTipo(Long residenciaId, String tipo) throws DataInvalidaException {
        try {
            if (tipo == null || tipo.isBlank()) {
                throw new DataInvalidaException("Tipo de quarto não pode ser nulo ou vazio.");
            }

            String tipoUpper = tipo.toUpperCase();
            List<Quarto> quartos = quartoRepository.findByResidenciaId(residenciaId);

            return quartos.stream()
                    .filter(q -> {
                        String tipoQuarto = q.getTipoQuarto();
                        if (tipoQuarto == null) {
                            tipoQuarto = determinarTipo(q);
                        }
                        return tipoQuarto.equals(tipoUpper);
                    })
                    .toList();
        } catch (NullPointerException e) {
            throw new DataInvalidaException("Erro ao filtrar quartos por tipo.", e);
        }
    }

    /**
     * Filtra quartos ativos de uma residência por tipo
     */
    public List<Quarto> filtrarPorTipoAtivos(Long residenciaId, String tipo) throws DataInvalidaException {
        List<Quarto> quartos = filtrarPorTipo(residenciaId, tipo);
        return quartos.stream()
                .filter(Quarto::isAtivo)
                .toList();
    }

    /**
     * Filtra quartos disponíveis de uma residência por tipo
     */
    public List<Quarto> filtrarPorTipoDisponiveis(Long residenciaId, String tipo, 
                                                    LocalDateTime dataInicio, LocalDateTime dataFim) 
            throws DataInvalidaException {
        if (dataInicio == null || dataFim == null) {
            throw new DataInvalidaException("Datas de início e fim não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new DataInvalidaException("Data de início não pode ser após a data de fim.");
        }

        List<Quarto> quartos = filtrarPorTipoAtivos(residenciaId, tipo);
        return quartos.stream()
                .filter(q -> q.verificarDisponibilidade(dataInicio, dataFim))
                .toList();
    }

    /**
     * Determina o tipo de um quarto com base em sua classe
     */
    private String determinarTipo(Quarto quarto) {
        if (quarto instanceof QuartoIndividual) {
            return "INDIVIDUAL";
        } else if (quarto instanceof QuartoDuplo) {
            return "DUPLO";
        } else if (quarto instanceof QuartoFamilia) {
            return "FAMILIA";
        }
        return "DESCONHECIDO";
    }

    /**
     * Lista todos os quartos de uma residência por tipo
     */
    public List<Quarto> listarQuartosPorTipo(Long residenciaId) {
        List<Quarto> quartos = quartoRepository.findByResidenciaId(residenciaId);
        return quartos.stream()
                .filter(Quarto::isAtivo)
                .toList();
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
