package com.hotelmarau;

import com.hotelmarau.exception.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para exceções customizadas.
 * Cobertura: criação, mensagens e propagação de exceções.
 */
public class ExcecoesCustomizadasTest {

    @Test
    public void testQuartoIndisponivelExceptionSemParametros() {
        QuartoIndisponivelException e = new QuartoIndisponivelException();
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("disponível"));
    }

    @Test
    public void testQuartoIndisponivelExceptionComMensagem() {
        String mensagem = "Quarto indisponível no período solicitado.";
        QuartoIndisponivelException e = new QuartoIndisponivelException(mensagem);
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    public void testQuartoIndisponivelExceptionComCausa() {
        Exception causa = new Exception("Causa da exceção");
        QuartoIndisponivelException e = new QuartoIndisponivelException("Mensagem", causa);
        assertEquals("Mensagem", e.getMessage());
        assertEquals(causa, e.getCause());
    }

    @Test
    public void testCapacidadeExcedidaExceptionSemParametros() {
        CapacidadeExcedidaException e = new CapacidadeExcedidaException();
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("capacidade"));
    }

    @Test
    public void testCapacidadeExcedidaExceptionComMensagem() {
        String mensagem = "O número de hóspedes (5) excede a capacidade (2).";
        CapacidadeExcedidaException e = new CapacidadeExcedidaException(mensagem);
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    public void testDataInvalidaExceptionSemParametros() {
        DataInvalidaException e = new DataInvalidaException();
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("Data"));
    }

    @Test
    public void testDataInvalidaExceptionComMensagem() {
        String mensagem = "Data de entrada não pode ser após a data de saída.";
        DataInvalidaException e = new DataInvalidaException(mensagem);
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    public void testRecursoNaoPermitidoExceptionSemParametros() {
        RecursoNaoPermitidoException e = new RecursoNaoPermitidoException();
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("recurso"));
    }

    @Test
    public void testRecursoNaoPermitidoExceptionComMensagem() {
        String mensagem = "Berço não é permitido para Quarto Individual.";
        RecursoNaoPermitidoException e = new RecursoNaoPermitidoException(mensagem);
        assertEquals(mensagem, e.getMessage());
    }

    @Test
    public void testExcecoesExtendemException() {
        assertTrue(new QuartoIndisponivelException() instanceof Exception);
        assertTrue(new CapacidadeExcedidaException() instanceof Exception);
        assertTrue(new DataInvalidaException() instanceof Exception);
        assertTrue(new RecursoNaoPermitidoException() instanceof Exception);
    }

    @Test
    public void testExcecoesChecked() {
        // Todas as exceções customizadas devem ser checked exceptions
        Exception e1 = new QuartoIndisponivelException();
        Exception e2 = new CapacidadeExcedidaException();
        Exception e3 = new DataInvalidaException();
        Exception e4 = new RecursoNaoPermitidoException();
        
        // Não são RuntimeException
        assertFalse(e1 instanceof RuntimeException);
        assertFalse(e2 instanceof RuntimeException);
        assertFalse(e3 instanceof RuntimeException);
        assertFalse(e4 instanceof RuntimeException);
    }

    @Test
    public void testMensagensSignificativas() {
        QuartoIndisponivelException e1 = new QuartoIndisponivelException("Quarto já reservado");
        assertTrue(e1.getMessage().contains("reservado"));
        
        CapacidadeExcedidaException e2 = new CapacidadeExcedidaException("4 pessoas em quarto de 2");
        assertTrue(e2.getMessage().contains("pessoas"));
        
        DataInvalidaException e3 = new DataInvalidaException("Datas inconsistentes");
        assertTrue(e3.getMessage().contains("inconsistentes"));
        
        RecursoNaoPermitidoException e4 = new RecursoNaoPermitidoException("TV a cabo para suíte");
        assertTrue(e4.getMessage().contains("cabo"));
    }
}
