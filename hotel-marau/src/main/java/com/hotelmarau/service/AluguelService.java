package com.hotelmarau.service;

import com.hotelmarau.dto.AluguelDTO;
import com.hotelmarau.exception.*;
import com.hotelmarau.model.*;
import com.hotelmarau.repository.AluguelRepository;
import com.hotelmarau.repository.ClienteRepository;
import com.hotelmarau.repository.QuartoRepository;
import com.hotelmarau.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ResidenciaRepository residenciaRepository;
    private final QuartoRepository quartoRepository;
    private final ClienteRepository clienteRepository;

    public List<Aluguel> listarTodos() {
        return aluguelRepository.findAll();
    }

    public List<Aluguel> listarPorCliente(Long clienteId) {
        return aluguelRepository.findByClienteId(clienteId);
    }

    public List<Aluguel> listarPorResidencia(Long residenciaId) {
        return aluguelRepository.findByResidenciaId(residenciaId);
    }

    public Aluguel buscarPorId(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado com id: " + id));
    }

    @Transactional
    public Aluguel criar(AluguelDTO dto) throws DataInvalidaException, QuartoIndisponivelException, 
                                                 CapacidadeExcedidaException, RecursoNaoPermitidoException {
        try {
            // Validação de datas
            validarDatas(dto.getDataEntrada(), dto.getDataSaida());

            // Busca entidades relacionadas
            Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Residência não encontrada."));
            
            Quarto quarto = quartoRepository.findById(dto.getQuartoId())
                    .orElseThrow(() -> new IllegalArgumentException("Quarto não encontrado."));
            
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

            if (residencia == null) {
                throw new DataInvalidaException("Residência não encontrada.");
            }
            if (quarto == null) {
                throw new DataInvalidaException("Quarto não encontrado.");
            }
            if (cliente == null) {
                throw new DataInvalidaException("Cliente não encontrado.");
            }

            // Validações de negócio
            if (!quarto.isAtivo()) {
                throw new QuartoIndisponivelException("O quarto está desativado e não pode ser reservado.");
            }

            if (!quarto.getResidencia().getId().equals(residencia.getId())) {
                throw new IllegalArgumentException("O quarto não pertence à residência informada.");
            }

            // Verifica disponibilidade no período
            boolean disponivel = quarto.verificarDisponibilidade(dto.getDataEntrada(), dto.getDataSaida());
            if (!disponivel) {
                throw new QuartoIndisponivelException("O quarto já está ocupado no período informado.");
            }

            // Validação de capacidade de hóspedes
            int capacidade = obterCapacidadeQuarto(quarto);
            if (dto.getNumeroHospedes() > capacidade) {
                throw new CapacidadeExcedidaException(
                    String.format("O número de hóspedes (%d) excede a capacidade do quarto (%d).",
                                 dto.getNumeroHospedes(), capacidade));
            }

            // Validação de berço: só QuartoDuplo e se o quarto tiver berço disponível
            if (dto.isBercoSolicitado()) {
                if (!(quarto instanceof QuartoDuplo duplo)) {
                    throw new RecursoNaoPermitidoException("Berço só pode ser solicitado para Quartos Duplos.");
                }
                if (!duplo.isTemBerco()) {
                    throw new RecursoNaoPermitidoException("Este quarto duplo não possui berço disponível.");
                }
            }

            // Cria o aluguel
            Aluguel aluguel = new Aluguel();
            aluguel.setResidencia(residencia);
            aluguel.setQuarto(quarto);
            aluguel.setCliente(cliente);
            aluguel.setDataEntrada(dto.getDataEntrada());
            aluguel.setDataSaida(dto.getDataSaida());
            aluguel.setNumeroHospedes(dto.getNumeroHospedes());
            aluguel.setBercoSolicitado(dto.isBercoSolicitado());
            aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);

            // Calcula diárias e valor final
            aluguel.calcularValorFinal();

            return aluguelRepository.save(aluguel);
        } catch (NullPointerException e) {
            throw new DataInvalidaException("Dados inválidos fornecidos.", e);
        }
    }

    @Transactional
    public Aluguel cancelar(Long id) throws IllegalArgumentException {
        try {
            Aluguel aluguel = buscarPorId(id);
            if (aluguel == null) {
                throw new IllegalArgumentException("Aluguel não encontrado.");
            }
            if (aluguel.getStatus() == Aluguel.StatusAluguel.CANCELADO) {
                throw new IllegalArgumentException("Aluguel já está cancelado.");
            }
            aluguel.cancelarReserva();
            return aluguelRepository.save(aluguel);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido fornecido.", e);
        }
    }

    @Transactional
    public Aluguel concluir(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        return aluguelRepository.save(aluguel);
    }

    /**
     * Retorna o recibo formatado de um aluguel
     */
    public String gerarRecibo(Long id) {
        Aluguel aluguel = buscarPorId(id);
        // Recalcula para garantir valores atualizados
        aluguel.calcularValorFinal();
        return aluguel.gerarRecibo();
    }

    /**
     * Valida as datas de entrada e saída
     */
    private void validarDatas(LocalDateTime dataEntrada, LocalDateTime dataSaida) throws DataInvalidaException {
        if (dataEntrada == null || dataSaida == null) {
            throw new DataInvalidaException("Datas de entrada e saída não podem ser nulas.");
        }
        if (dataEntrada.isAfter(dataSaida)) {
            throw new DataInvalidaException("Data de entrada não pode ser após a data de saída.");
        }
        if (dataEntrada.isEqual(dataSaida)) {
            throw new DataInvalidaException("Data de entrada e saída não podem ser iguais.");
        }
        if (dataEntrada.isBefore(LocalDateTime.now())) {
            throw new DataInvalidaException("Data de entrada não pode ser no passado.");
        }
    }

    /**
     * Obtém a capacidade de hóspedes de um quarto
     */
    private int obterCapacidadeQuarto(Quarto quarto) {
        if (quarto instanceof QuartoIndividual qi) {
            return 1;
        } else if (quarto instanceof QuartoDuplo qd) {
            return 2;
        } else if (quarto instanceof QuartoFamilia qf) {
            return qf.calcularCapacidadeMaxima();
        }
        return 1;
    }

    /**
     * Lista aluguéis de um cliente com filtros opcionais
     */
    public List<Aluguel> listarHistoricoCliente(Long clienteId) {
        return aluguelRepository.findByClienteId(clienteId);
    }

    /**
     * Lista aluguéis ativos de um cliente
     */
    public List<Aluguel> listarAlugueisAtivosCliente(Long clienteId) {
        List<Aluguel> alugueis = aluguelRepository.findByClienteId(clienteId);
        return alugueis.stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONFIRMADO)
                .toList();
    }
}
