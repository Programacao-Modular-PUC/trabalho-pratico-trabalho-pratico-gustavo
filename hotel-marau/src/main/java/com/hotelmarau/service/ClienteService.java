package com.hotelmarau.service;

import com.hotelmarau.dto.ClienteDTO;
import com.hotelmarau.model.Aluguel;
import com.hotelmarau.model.Cliente;
import com.hotelmarau.repository.AluguelRepository;
import com.hotelmarau.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AluguelRepository aluguelRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com CPF: " + cpf));
    }

    @Transactional
    public Cliente criar(ClienteDTO dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Já existe um cliente cadastrado com o CPF: " + dto.getCpf());
        }
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEndereco(dto.getEndereco());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);
        cliente.atualizarDados(dto.getNome(), dto.getEndereco(), dto.getTelefone(), dto.getEmail());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        clienteRepository.deleteById(id);
    }

    /**
     * Obtém o histórico completo de um cliente (todas as reservas/aluguéis)
     * @param clienteId ID do cliente
     * @return Lista de todos os aluguéis do cliente
     */
    public List<Aluguel> obterHistoricoCliente(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        return aluguelRepository.findByClienteId(clienteId);
    }

    /**
     * Obtém apenas os aluguéis ativos de um cliente
     */
    public List<Aluguel> obterAlugueisAtivos(Long clienteId) {
        List<Aluguel> alugueis = obterHistoricoCliente(clienteId);
        return alugueis.stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONFIRMADO)
                .toList();
    }

    /**
     * Obtém apenas os aluguéis concluídos de um cliente
     */
    public List<Aluguel> obterAluguelsConcluidos(Long clienteId) {
        List<Aluguel> alugueis = obterHistoricoCliente(clienteId);
        return alugueis.stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONCLUIDO)
                .toList();
    }

    /**
     * Obtém apenas os aluguéis cancelados de um cliente
     */
    public List<Aluguel> obterAluguelsCancelados(Long clienteId) {
        List<Aluguel> alugueis = obterHistoricoCliente(clienteId);
        return alugueis.stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CANCELADO)
                .toList();
    }

    /**
     * Gera um relatório formatado do histórico de um cliente
     */
    public String gerarRelatoriHistorico(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);
        List<Aluguel> alugueis = obterHistoricoCliente(clienteId);

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("========================================\n");
        relatorio.append("     HISTÓRICO DE HOSPEDAGEM\n");
        relatorio.append("========================================\n");
        relatorio.append("Cliente: ").append(cliente.getNome()).append("\n");
        relatorio.append("CPF: ").append(cliente.getCpf()).append("\n");
        relatorio.append("Email: ").append(cliente.getEmail()).append("\n");
        relatorio.append("----------------------------------------\n");

        if (alugueis.isEmpty()) {
            relatorio.append("Nenhuma reserva encontrada.\n");
        } else {
            relatorio.append("Total de reservas: ").append(alugueis.size()).append("\n\n");
            for (Aluguel aluguel : alugueis) {
                relatorio.append("Reserva ID: ").append(aluguel.getId()).append("\n");
                relatorio.append("  Quarto: ").append(aluguel.getQuarto().getTipoQuarto()).append("\n");
                relatorio.append("  Check-in: ").append(aluguel.getDataEntrada()).append("\n");
                relatorio.append("  Check-out: ").append(aluguel.getDataSaida()).append("\n");
                relatorio.append("  Hóspedes: ").append(aluguel.getNumeroHospedes()).append("\n");
                relatorio.append("  Valor: R$ ").append(String.format("%.2f", aluguel.getValorFinal())).append("\n");
                relatorio.append("  Status: ").append(aluguel.getStatus()).append("\n");
                relatorio.append("\n");
            }
        }

        relatorio.append("========================================\n");
        return relatorio.toString();
    }
}
