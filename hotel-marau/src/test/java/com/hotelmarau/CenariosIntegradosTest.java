package com.hotelmarau;

import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes integrados de cenários complexos.
 * Cobertura: múltiplas regras aplicadas simultaneamente.
 */
public class CenariosIntegradosTest {

    private Residencia residencia;
    private QuartoDuplo quartoDuplo;
    private Cliente cliente;
    private LocalDateTime dataBase;

    @BeforeEach
    public void setup() {
        // Setup básico
        residencia = new Residencia();
        residencia.setId(1L);
        residencia.setEndereco("Rua Principal");
        residencia.setNumero("123");
        residencia.setBairro("Centro");
        residencia.setCep("12345-678");
        residencia.setTelefone("11999999999");
        residencia.setEmail("hotel@example.com");
        
        quartoDuplo = new QuartoDuplo(150.0, true, true, QuartoDuplo.TipoCama.QUEEN, true);
        quartoDuplo.setId(1L);
        quartoDuplo.setResidencia(residencia);
        quartoDuplo.setAlugueis(new ArrayList<>());
        
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setCpf("12345678901");
        
        dataBase = LocalDateTime.of(2025, 1, 15, 12, 0);
    }

    @Test
    public void testAluguelCompletoValido() {
        Aluguel aluguel = new Aluguel();
        aluguel.setResidencia(residencia);
        aluguel.setQuarto(quartoDuplo);
        aluguel.setCliente(cliente);
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(3));
        aluguel.setNumeroHospedes(2);
        aluguel.setBercoSolicitado(true);
        aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        // Calcula valor final
        double valor = aluguel.calcularValorFinal();
        
        // Validações
        assertEquals(Aluguel.StatusAluguel.CONFIRMADO, aluguel.getStatus());
        assertEquals(3, aluguel.calcularDiarias());
        assertTrue(valor > 0);
        assertEquals(2, aluguel.getNumeroHospedes());
        assertTrue(aluguel.isBercoSolicitado());
    }

    @Test
    public void testDisponivelParaNovoAluguel() {
        // Primeiro aluguel
        Aluguel primeiroAluguel = new Aluguel();
        primeiroAluguel.setDataEntrada(dataBase);
        primeiroAluguel.setDataSaida(dataBase.plusDays(3));
        primeiroAluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        quartoDuplo.getAlugueis().add(primeiroAluguel);
        
        // Tentativa de novo aluguel após o primeiro
        boolean disponivel = quartoDuplo.verificarDisponibilidade(
            dataBase.plusDays(4),
            dataBase.plusDays(6)
        );
        
        assertTrue(disponivel, "Quarto deve estar disponível após primeiro aluguel");
    }

    @Test
    public void testCancelamentoLiberaQuarto() {
        Aluguel aluguel = new Aluguel();
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(3));
        aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        quartoDuplo.getAlugueis().add(aluguel);
        
        // Quarto não deve estar disponível
        boolean disponivel1 = quartoDuplo.verificarDisponibilidade(
            dataBase.plusDays(1),
            dataBase.plusDays(2)
        );
        assertFalse(disponivel1);
        
        // Cancela aluguel
        aluguel.cancelarReserva();
        
        // Agora deve estar disponível
        boolean disponivel2 = quartoDuplo.verificarDisponibilidade(
            dataBase.plusDays(1),
            dataBase.plusDays(2)
        );
        assertTrue(disponivel2, "Quarto deve estar disponível após cancelamento");
    }

    @Test
    public void testHistoricoClienteMultiplosAlugueis() {
        // Cliente faz 3 reservas
        for (int i = 0; i < 3; i++) {
            Aluguel aluguel = new Aluguel();
            aluguel.setId((long) i);
            aluguel.setCliente(cliente);
            aluguel.setDataEntrada(dataBase.plusDays(i * 5));
            aluguel.setDataSaida(dataBase.plusDays(i * 5 + 2));
            aluguel.setNumeroHospedes(2);
            aluguel.setValorFinal(450.0);
            aluguel.setStatus(i == 1 ? Aluguel.StatusAluguel.CANCELADO : Aluguel.StatusAluguel.CONFIRMADO);
            
            cliente.getAlugueis().add(aluguel);
        }
        
        // Verifica histórico
        assertEquals(3, cliente.getAlugueis().size());
        
        List<Aluguel> confirmados = cliente.getAlugueis().stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONFIRMADO)
                .toList();
        assertEquals(2, confirmados.size());
        
        List<Aluguel> cancelados = cliente.getAlugueis().stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CANCELADO)
                .toList();
        assertEquals(1, cancelados.size());
    }

    @Test
    public void testCapacidadeVsAdicionais() {
        // Quarto Família
        QuartoFamilia familia = new QuartoFamilia(200.0, true, true, 2, 1, 1, 2);
        
        // Máxima capacidade
        int capacidade = familia.calcularCapacidadeMaxima();
        assertEquals(6, capacidade);
        
        // Valor com 6 pessoas
        double valorComGrupo = familia.calcularValorTotal(1, 6);
        
        // Valor com 2 pessoas
        double valorSemGrupo = familia.calcularValorTotal(1, 2);
        
        // Com desconto para grupos, pode ser mais vantajoso
        assertTrue(valorComGrupo > 0);
        assertTrue(valorSemGrupo > 0);
    }

    @Test
    public void testReciboFormatado() {
        Aluguel aluguel = new Aluguel();
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(2));
        aluguel.setQuarto(quartoDuplo);
        aluguel.setNumeroHospedes(2);
        aluguel.setBercoSolicitado(true);
        aluguel.calcularValorFinal();
        
        String recibo = aluguel.gerarRecibo();
        
        assertNotNull(recibo);
        assertTrue(recibo.contains("RECIBO"));
        assertTrue(recibo.contains("entrada"));
        assertTrue(recibo.contains("saída"));
        assertTrue(recibo.contains("R$"));
        assertTrue(recibo.length() > 0);
    }
}
