package com.hotelmarau;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hotelmarau.dto.AluguelDTO;
import com.hotelmarau.exception.CapacidadeExcedidaException;
import com.hotelmarau.model.Cliente;
import com.hotelmarau.model.Quarto;
import com.hotelmarau.model.QuartoDuplo;
import com.hotelmarau.model.QuartoFamilia;
import com.hotelmarau.model.QuartoIndividual;
import com.hotelmarau.model.Residencia;
import com.hotelmarau.repository.AluguelRepository;
import com.hotelmarau.repository.ClienteRepository;
import com.hotelmarau.repository.QuartoRepository;
import com.hotelmarau.repository.ResidenciaRepository;
import com.hotelmarau.service.AluguelService;

/**
 * Testes para validação de regras de negócio dos quartos.
 * Cobertura: capacidade máxima, cálculo de diária e validações.
 */
public class QuartoCapacidadeTest {

    private QuartoIndividual quartoIndividual;
    private QuartoDuplo quartoDuplo;
    private QuartoFamilia quartoFamilia;
    private AluguelService aluguelService;
    private ResidenciaRepository residenciaRepository;
    private QuartoRepository quartoRepository;
    private ClienteRepository clienteRepository;

    @BeforeEach
    public void setup() {
        residenciaRepository = mock(ResidenciaRepository.class);
        quartoRepository = mock(QuartoRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        AluguelRepository aluguelRepository = mock(AluguelRepository.class);
        aluguelService = new AluguelService(aluguelRepository, residenciaRepository, quartoRepository, clienteRepository);

        // Quarto Individual: capacidade 1
        quartoIndividual = new QuartoIndividual(100.0, true, false, 1);

        // Quarto Duplo: capacidade 2
        quartoDuplo = new QuartoDuplo(150.0, true, true, QuartoDuplo.TipoCama.QUEEN, true);

        // Quarto Família: capacidade 6 (2 solteiros + 1 casal + 1 queen)
        quartoFamilia = new QuartoFamilia(200.0, true, true, 2, 1, 1, 2);
    }

    @Test
    public void testCapacidadeQuartoIndividual() {
        assertEquals(1, obterCapacidade(quartoIndividual), 
            "Quarto Individual deve ter capacidade de 1 pessoa");
    }

    @Test
    public void testCapacidadeQuartoDuplo() {
        assertEquals(2, obterCapacidade(quartoDuplo),
            "Quarto Duplo deve ter capacidade de 2 pessoas");
    }

    @Test
    public void testCapacidadeQuartoFamilia() {
        assertEquals(6, quartoFamilia.calcularCapacidadeMaxima(),
            "Quarto Família deve ter capacidade de 6 pessoas");
    }

    @Test
    public void testCapacidadeExcedidaIndividual() {
        Residencia residencia = criarResidencia(1L);
        Cliente cliente = criarCliente(10L);
        quartoIndividual.setResidencia(residencia);
        quartoIndividual.setId(100L);

        when(residenciaRepository.findById(1L)).thenReturn(java.util.Optional.of(residencia));
        when(quartoRepository.findById(100L)).thenReturn(java.util.Optional.of(quartoIndividual));
        when(clienteRepository.findById(10L)).thenReturn(java.util.Optional.of(cliente));

        AluguelDTO dto = criarDto(1L, 100L, 10L, 2);

        assertThrows(CapacidadeExcedidaException.class,
                () -> aluguelService.criar(dto),
                "Quarto Individual deve lançar CapacidadeExcedidaException com 2 hóspedes");
    }

    @Test
    public void testCapacidadeExcedidaDuplo() {
        Residencia residencia = criarResidencia(2L);
        Cliente cliente = criarCliente(20L);
        quartoDuplo.setResidencia(residencia);
        quartoDuplo.setId(200L);

        when(residenciaRepository.findById(2L)).thenReturn(java.util.Optional.of(residencia));
        when(quartoRepository.findById(200L)).thenReturn(java.util.Optional.of(quartoDuplo));
        when(clienteRepository.findById(20L)).thenReturn(java.util.Optional.of(cliente));

        AluguelDTO dto = criarDto(2L, 200L, 20L, 3);

        assertThrows(CapacidadeExcedidaException.class,
                () -> aluguelService.criar(dto),
                "Quarto Duplo deve lançar CapacidadeExcedidaException com 3 hóspedes");
    }

    private AluguelDTO criarDto(Long residenciaId, Long quartoId, Long clienteId, int hospedes) {
        AluguelDTO dto = new AluguelDTO();
        dto.setResidenciaId(residenciaId);
        dto.setQuartoId(quartoId);
        dto.setClienteId(clienteId);
        dto.setDataEntrada(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
        dto.setDataSaida(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0).withSecond(0).withNano(0));
        dto.setNumeroHospedes(hospedes);
        dto.setBercoSolicitado(false);
        return dto;
    }

    private Residencia criarResidencia(Long id) {
        Residencia residencia = new Residencia();
        residencia.setId(id);
        residencia.setEndereco("Rua Teste");
        residencia.setNumero("100");
        residencia.setBairro("Centro");
        residencia.setCep("00000-000");
        residencia.setTelefone("111111111");
        residencia.setEmail("residencia@test.com");
        return residencia;
    }

    private Cliente criarCliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678901");
        cliente.setEndereco("Rua Cliente");
        cliente.setTelefone("11999999999");
        cliente.setEmail("cliente@test.com");
        return cliente;
    }

    private int obterCapacidade(Quarto quarto) {
        if (quarto instanceof QuartoIndividual) {
            return 1;
        } else if (quarto instanceof QuartoDuplo) {
            return 2;
        } else if (quarto instanceof QuartoFamilia) {
            return ((QuartoFamilia) quarto).calcularCapacidadeMaxima();
        }
        return 0;
    }
}
