package com.hotelmarau;

import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para histórico de hóspedes.
 * Cobertura: listagem de aluguéis, filtros por status.
 */
public class HistoricoClienteTest {

    private Cliente cliente;
    private List<Aluguel> alugueis;
    private LocalDateTime dataBase;

    @BeforeEach
    public void setup() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("joao@example.com");
        cliente.setTelefone("11999999999");
        
        alugueis = new ArrayList<>();
        dataBase = LocalDateTime.of(2025, 1, 15, 12, 0);
    }

    @Test
    public void testHistoricoVazioNovoCliente() {
        List<Aluguel> historico = cliente.listarReservas();
        assertTrue(historico.isEmpty(),
            "Cliente novo deve ter histórico vazio");
    }

    @Test
    public void testAdicionarAluguelAoHistorico() {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setCliente(cliente);
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(2));
        
        cliente.getAlugueis().add(aluguel);
        
        assertEquals(1, cliente.getAlugueis().size(),
            "Cliente deve ter 1 aluguel no histórico");
    }

    @Test
    public void testMultiplosAlugueisNoHistorico() {
        for (int i = 0; i < 5; i++) {
            Aluguel aluguel = new Aluguel();
            aluguel.setId((long) i);
            aluguel.setCliente(cliente);
            aluguel.setDataEntrada(dataBase.plusDays(i * 5));
            aluguel.setDataSaida(dataBase.plusDays(i * 5 + 2));
            cliente.getAlugueis().add(aluguel);
        }
        
        assertEquals(5, cliente.getAlugueis().size(),
            "Cliente deve ter 5 aluguéis no histórico");
    }

    @Test
    public void testFiltrarAlugueisAtivos() {
        // Adiciona aluguéis com diferentes status
        Aluguel confirmado = new Aluguel();
        confirmado.setId(1L);
        confirmado.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        Aluguel cancelado = new Aluguel();
        cancelado.setId(2L);
        cancelado.setStatus(Aluguel.StatusAluguel.CANCELADO);
        
        Aluguel concluido = new Aluguel();
        concluido.setId(3L);
        concluido.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        
        cliente.getAlugueis().addAll(List.of(confirmado, cancelado, concluido));
        
        List<Aluguel> ativos = cliente.getAlugueis().stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONFIRMADO)
                .toList();
        
        assertEquals(1, ativos.size(),
            "Deve haver apenas 1 aluguel ativo");
        assertEquals(1L, ativos.get(0).getId());
    }

    @Test
    public void testFiltrarAluguelsCancelados() {
        Aluguel confirmado = new Aluguel();
        confirmado.setId(1L);
        confirmado.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        Aluguel cancelado1 = new Aluguel();
        cancelado1.setId(2L);
        cancelado1.setStatus(Aluguel.StatusAluguel.CANCELADO);
        
        Aluguel cancelado2 = new Aluguel();
        cancelado2.setId(3L);
        cancelado2.setStatus(Aluguel.StatusAluguel.CANCELADO);
        
        cliente.getAlugueis().addAll(List.of(confirmado, cancelado1, cancelado2));
        
        List<Aluguel> cancelados = cliente.getAlugueis().stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CANCELADO)
                .toList();
        
        assertEquals(2, cancelados.size(),
            "Deve haver 2 aluguéis cancelados");
    }

    @Test
    public void testFiltrarAluguelsConcluidos() {
        Aluguel confirmado = new Aluguel();
        confirmado.setId(1L);
        confirmado.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        Aluguel concluido1 = new Aluguel();
        concluido1.setId(2L);
        concluido1.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        
        Aluguel concluido2 = new Aluguel();
        concluido2.setId(3L);
        concluido2.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        
        cliente.getAlugueis().addAll(List.of(confirmado, concluido1, concluido2));
        
        List<Aluguel> concluidos = cliente.getAlugueis().stream()
                .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CONCLUIDO)
                .toList();
        
        assertEquals(2, concluidos.size(),
            "Deve haver 2 aluguéis concluídos");
    }

    @Test
    public void testInformacoesAluguelNoHistorico() {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setCliente(cliente);
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(2));
        aluguel.setNumeroHospedes(2);
        aluguel.setValorFinal(450.0);
        aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        
        cliente.getAlugueis().add(aluguel);
        
        Aluguel recuperado = cliente.getAlugueis().get(0);
        assertEquals("João Silva", cliente.getNome());
        assertEquals(2, recuperado.getNumeroHospedes());
        assertEquals(450.0, recuperado.getValorFinal());
        assertEquals(Aluguel.StatusAluguel.CONFIRMADO, recuperado.getStatus());
    }
}
