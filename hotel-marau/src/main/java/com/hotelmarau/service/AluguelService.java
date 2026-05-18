package com.hotelmarau.service;

import com.hotelmarau.dto.AluguelDTO;
import com.hotelmarau.model.*;
import com.hotelmarau.repository.AluguelRepository;
import com.hotelmarau.repository.ClienteRepository;
import com.hotelmarau.repository.QuartoRepository;
import com.hotelmarau.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Aluguel criar(AluguelDTO dto) {
        // Busca entidades relacionadas
        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
                .orElseThrow(() -> new RuntimeException("Residência não encontrada."));
        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado."));
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        // Validações de negócio
        if (!quarto.isAtivo()) {
            throw new IllegalStateException("O quarto está desativado e não pode ser reservado.");
        }

        if (!quarto.getResidencia().getId().equals(residencia.getId())) {
            throw new IllegalArgumentException("O quarto não pertence à residência informada.");
        }

        // Verifica disponibilidade no período
        boolean disponivel = quarto.verificarDisponibilidade(dto.getDataEntrada(), dto.getDataSaida());
        if (!disponivel) {
            throw new IllegalStateException("O quarto já está ocupado no período informado.");
        }

        // Validação de berço: só QuartoDuplo e se o quarto tiver berço disponível
        if (dto.isBercoSolicitado()) {
            if (!(quarto instanceof QuartoDuplo duplo)) {
                throw new IllegalArgumentException("Berço só pode ser solicitado para Quartos Duplos.");
            }
            if (!duplo.isTemBerco()) {
                throw new IllegalArgumentException("Este quarto duplo não possui berço disponível.");
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
    }

    @Transactional
    public Aluguel cancelar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        if (aluguel.getStatus() == Aluguel.StatusAluguel.CANCELADO) {
            throw new IllegalStateException("Aluguel já está cancelado.");
        }
        aluguel.cancelarReserva();
        return aluguelRepository.save(aluguel);
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
}
