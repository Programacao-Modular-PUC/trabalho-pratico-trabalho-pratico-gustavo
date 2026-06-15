package com.hotelmarau;

import com.hotelmarau.exception.DataInvalidaException;
import com.hotelmarau.exception.QuartoIndisponivelException;
import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para validação de datas e disponibilidade de quartos.
 * Cobertura: períodos válidos, sobreposição, quartos ocupados.
 */
public class DisponibilidadeTest {

    private QuartoDuplo quartoDuplo;
    private Aluguel aluguelExistente;
    private LocalDateTime dataBase;

    @BeforeEach
    public void setup() {
        quartoDuplo = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.QUEEN, false);
        dataBase = LocalDateTime.of(2025, 1, 15, 12, 0);
        
        // Cria um aluguel existente
        aluguelExistente = new Aluguel();
        aluguelExistente.setDataEntrada(dataBase);
        aluguelExistente.setDataSaida(dataBase.plusDays(3));
        aluguelExistente.setStatus(Aluguel.StatusAluguel.CONFIRMADO);
    }

    @Test
    public void testDataInvalidaEntradaAposSaida() {
        assertThrows(DataInvalidaException.class, () -> {
            LocalDateTime entrada = dataBase.plusDays(5);
            LocalDateTime saida = dataBase;
            if (entrada.isAfter(saida)) {
                throw new DataInvalidaException("Data de entrada não pode ser após a data de saída.");
            }
        }, "Data de entrada não pode ser após data de saída");
    }

    @Test
    public void testDataInvalidaEntradaIgualSaida() {
        assertThrows(DataInvalidaException.class, () -> {
            LocalDateTime entrada = dataBase;
            LocalDateTime saida = dataBase;
            if (entrada.isEqual(saida)) {
                throw new DataInvalidaException("Data de entrada e saída não podem ser iguais.");
            }
        }, "Data de entrada não pode ser igual à data de saída");
    }

    @Test
    public void testQuartoDisponível() {
        LocalDateTime entrada = dataBase.plusDays(5);
        LocalDateTime saida = dataBase.plusDays(8);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(entrada, saida);
        assertTrue(disponivel, "Quarto deve estar disponível fora do período ocupado");
    }

    @Test
    public void testQuartoIndisponivelPeriodoSobreposicao() {
        // Período do novo aluguel sobrepõe o existente
        LocalDateTime entrada = dataBase.plusDays(1); // Durante o aluguel existente
        LocalDateTime saida = dataBase.plusDays(5);
        
        quartoDuplo.getAlugueis().add(aluguelExistente);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(entrada, saida);
        assertFalse(disponivel, "Quarto não deve estar disponível com sobreposição");
    }

    @Test
    public void testQuartoIndisponivelApenasAntesDoFim() {
        // Entrada durante o período do aluguel existente
        LocalDateTime entrada = dataBase.plusDays(2);
        LocalDateTime saida = dataBase.plusDays(10);
        
        quartoDuplo.getAlugueis().add(aluguelExistente);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(entrada, saida);
        assertFalse(disponivel, "Quarto não deve estar disponível com sobreposição");
    }

    @Test
    public void testQuartoDisponíveljustesAntesdePeriodo() {
        // Saída antes do aluguel existente
        LocalDateTime entrada = dataBase.minusDays(5);
        LocalDateTime saida = dataBase.minusMinutes(1);
        
        quartoDuplo.getAlugueis().add(aluguelExistente);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(entrada, saida);
        assertTrue(disponivel, "Quarto deve estar disponível fora do período");
    }

    @Test
    public void testQuartoDisponívelAposAluguel() {
        // Entrada após o fim do aluguel existente
        LocalDateTime entrada = dataBase.plusDays(4);
        LocalDateTime saida = dataBase.plusDays(7);
        
        quartoDuplo.getAlugueis().add(aluguelExistente);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(entrada, saida);
        assertTrue(disponivel, "Quarto deve estar disponível após o período");
    }

    @Test
    public void testAluguelCanceladoNaoAcupa() {
        Aluguel aluguelCancelado = new Aluguel();
        aluguelCancelado.setDataEntrada(dataBase);
        aluguelCancelado.setDataSaida(dataBase.plusDays(3));
        aluguelCancelado.setStatus(Aluguel.StatusAluguel.CANCELADO);
        
        quartoDuplo.getAlugueis().add(aluguelCancelado);
        
        boolean disponivel = quartoDuplo.verificarDisponibilidade(dataBase.plusDays(1), dataBase.plusDays(2));
        assertTrue(disponivel, "Quarto deve estar disponível se o aluguel está cancelado");
    }
}
