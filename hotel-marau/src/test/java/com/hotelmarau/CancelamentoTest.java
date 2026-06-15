package com.hotelmarau;

import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para cancelamento de aluguéis.
 * Cobertura: mudança de status, dados preservados, operações inválidas.
 */
public class CancelamentoTest {

    private Aluguel aluguel;
    private LocalDateTime dataBase;

    @BeforeEach
    public void setup() {
        dataBase = LocalDateTime.of(2025, 1, 15, 12, 0);
        
        aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setNumeroHospedes(2);
        aluguel.setDataEntrada(dataBase);
        aluguel.setDataSaida(dataBase.plusDays(3));
        aluguel.setValorFinal(450.0);
        aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
    }

    @Test
    public void testCancelamentoMudaStatus() {
        assertEquals(Aluguel.StatusAluguel.CONFIRMADO, aluguel.getStatus(),
            "Status inicial deve ser CONFIRMADO");
        
        aluguel.cancelarReserva();
        
        assertEquals(Aluguel.StatusAluguel.CANCELADO, aluguel.getStatus(),
            "Status deve mudar para CANCELADO após cancelamento");
    }

    @Test
    public void testDadosPreservadosAposCancelamento() {
        Long idOriginal = aluguel.getId();
        int hospedesdOriginal = aluguel.getNumeroHospedes();
        double valorOriginal = aluguel.getValorFinal();
        
        aluguel.cancelarReserva();
        
        assertEquals(idOriginal, aluguel.getId(),
            "ID deve ser preservado após cancelamento");
        assertEquals(hospedesdOriginal, aluguel.getNumeroHospedes(),
            "Número de hóspedes deve ser preservado");
        assertEquals(valorOriginal, aluguel.getValorFinal(),
            "Valor deve ser preservado após cancelamento");
    }

    @Test
    public void testCancelamentoJaCancelado() {
        aluguel.cancelarReserva();
        
        assertEquals(Aluguel.StatusAluguel.CANCELADO, aluguel.getStatus(),
            "Primeiro cancelamento deve funcionar");
        
        // Segundo cancelamento mantém status CANCELADO
        aluguel.cancelarReserva();
        
        assertEquals(Aluguel.StatusAluguel.CANCELADO, aluguel.getStatus(),
            "Cancelamento múltiplo mantém status CANCELADO");
    }

    @Test
    public void testConfirmarReservaApósCancelamento() {
        aluguel.cancelarReserva();
        
        assertEquals(Aluguel.StatusAluguel.CANCELADO, aluguel.getStatus());
        
        // Confirmando novamente
        aluguel.confirmarReserva();
        
        assertEquals(Aluguel.StatusAluguel.CONFIRMADO, aluguel.getStatus(),
            "Reserva pode ser confirmada novamente após cancelamento");
    }

    @Test
    public void testConcluirAluguel() {
        aluguel.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        
        assertEquals(Aluguel.StatusAluguel.CONCLUIDO, aluguel.getStatus(),
            "Status pode ser alterado para CONCLUIDO");
    }

    @Test
    public void testTransicoesStatus() {
        // CONFIRMADO -> CANCELADO
        aluguel.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
        aluguel.cancelarReserva();
        assertEquals(Aluguel.StatusAluguel.CANCELADO, aluguel.getStatus());
        
        // CANCELADO -> CONFIRMADO
        aluguel.confirmarReserva();
        assertEquals(Aluguel.StatusAluguel.CONFIRMADO, aluguel.getStatus());
        
        // CONFIRMADO -> CONCLUIDO
        aluguel.setStatus(Aluguel.StatusAluguel.CONCLUIDO);
        assertEquals(Aluguel.StatusAluguel.CONCLUIDO, aluguel.getStatus());
    }
}
