package com.hotelmarau.service;

import com.hotelmarau.dto.QuartoDetalhesDTO;
import com.hotelmarau.dto.ResidenciaDetalhesDTO;
import com.hotelmarau.dto.ResidenciaDTO;
import com.hotelmarau.model.*;
import com.hotelmarau.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidenciaService {

    private final ResidenciaRepository residenciaRepository;

    public List<ResidenciaDetalhesDTO> listarTodasDetalhes() {
        return residenciaRepository.findAll().stream()
                .map(this::toDetalhesDTO)
                .toList();
    }

    public ResidenciaDetalhesDTO buscarPorIdDetalhes(Long id) {
        Residencia residencia = residenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada com id: " + id));
        return toDetalhesDTO(residencia);
    }

    @Transactional
    public ResidenciaDetalhesDTO criar(ResidenciaDTO dto) {
        Residencia residencia = new Residencia();
        residencia.setEndereco(dto.getEndereco());
        residencia.setNumero(dto.getNumero());
        residencia.setBairro(dto.getBairro());
        residencia.setCep(dto.getCep());
        residencia.setTelefone(dto.getTelefone());
        residencia.setEmail(dto.getEmail());

        Residencia saved = residenciaRepository.save(residencia);
        return toDetalhesDTO(saved);
    }

    @Transactional
    public ResidenciaDetalhesDTO atualizar(Long id, ResidenciaDTO dto) {
        Residencia residencia = buscarPorIdDetalhesSemDTO(id);

        residencia.setEndereco(dto.getEndereco());
        residencia.setNumero(dto.getNumero());
        residencia.setBairro(dto.getBairro());
        residencia.setCep(dto.getCep());
        residencia.setTelefone(dto.getTelefone());
        residencia.setEmail(dto.getEmail());

        Residencia saved = residenciaRepository.save(residencia);
        return toDetalhesDTO(saved);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorIdDetalhesSemDTO(id);
        residenciaRepository.deleteById(id);
    }

    private Residencia buscarPorIdDetalhesSemDTO(Long id) {
        return residenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Residência não encontrada com id: " + id));
    }

    private ResidenciaDetalhesDTO toDetalhesDTO(Residencia residencia) {
        ResidenciaDetalhesDTO dto = new ResidenciaDetalhesDTO();
        dto.setId(residencia.getId());
        dto.setEndereco(residencia.getEndereco());
        dto.setNumero(residencia.getNumero());
        dto.setBairro(residencia.getBairro());
        dto.setCep(residencia.getCep());
        dto.setTelefone(residencia.getTelefone());
        dto.setEmail(residencia.getEmail());

        // quartos é LAZY: como os endpoints de criar/atualizar são @Transactional e para leitura
        // faremos acesso no mesmo contexto da requisição.
        List<QuartoDetalhesDTO> quartosDetalhes = residencia.getQuartos().stream()
                .map(quarto -> toQuartoDetalhesDTO(quarto))
                .toList();

        dto.setQuartos(quartosDetalhes);
        return dto;
    }

    private QuartoDetalhesDTO toQuartoDetalhesDTO(Quarto quarto) {
        QuartoDetalhesDTO dto = new QuartoDetalhesDTO();
        dto.setId(quarto.getId());
        dto.setTipo(quarto.getTipoQuarto());
        dto.setValorBase(quarto.getValorBase());
        dto.setPossuiAr(quarto.isPossuiAr());
        dto.setPossuiHidro(quarto.isPossuiHidro());
        dto.setAtivo(quarto.isAtivo());

        if (quarto instanceof QuartoIndividual individual) {
            dto.setNumeroCamas(individual.getNumeroCamas());
        } else if (quarto instanceof QuartoDuplo duplo) {
            dto.setTipoCama(mapTipoCama(duplo.getTipoCama()));
            dto.setTemBerco(duplo.isTemBerco());
        } else if (quarto instanceof QuartoFamilia familia) {
            dto.setCamasSolteiro(familia.getCamasSolteiro());
            dto.setCamasCasal(familia.getCamasCasal());
            dto.setCamasQueenKing(familia.getCamasQueenKing());
            dto.setCapacidadeMaxima(familia.getCapacidadeMaxima());
            dto.setNumeroAmbientes(familia.getNumeroAmbientes());
        }

        return dto;
    }

    private QuartoDetalhesDTO.QuartoDuploTipoCama mapTipoCama(QuartoDuplo.TipoCama tipoCama) {
        if (tipoCama == null) return null;

        return switch (tipoCama) {
            case CASAL_COMUM -> QuartoDetalhesDTO.QuartoDuploTipoCama.CASAL_COMUM;
            case QUEEN -> QuartoDetalhesDTO.QuartoDuploTipoCama.QUEEN;
            case KING -> QuartoDetalhesDTO.QuartoDuploTipoCama.KING;
        };
    }
}

