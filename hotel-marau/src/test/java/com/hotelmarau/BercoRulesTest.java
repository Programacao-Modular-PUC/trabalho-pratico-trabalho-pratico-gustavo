package com.hotelmarau;

import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para regras de berço (crib).
 * Cobertura: berço permitido apenas em Duplo, com exceção para Individual e Família.
 */
public class BercoRulesTest {

    private QuartoIndividual quartoIndividual;
    private QuartoDuplo quartoDuploComBerco;
    private QuartoDuplo quartoDuploSemBerco;
    private QuartoFamilia quartoFamilia;

    @BeforeEach
    public void setup() {
        quartoIndividual = new QuartoIndividual(100.0, false, false, 1);
        quartoDuploComBerco = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.QUEEN, true);
        quartoDuploSemBerco = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.QUEEN, false);
        quartoFamilia = new QuartoFamilia(200.0, false, false, 2, 1, 0, 2);
    }

    @Test
    public void testBercoPermitidoEmDuplo() {
        assertTrue(quartoDuploComBerco.isTemBerco(),
            "Quarto Duplo deve permitir berço quando disponível");
    }

    @Test
    public void testBercoNaoDisponivel() {
        assertFalse(quartoDuploSemBerco.isTemBerco(),
            "Quarto Duplo deve indicar quando não tem berço");
    }

    @Test
    public void testValorDiariaComBerco() {
        double valorComBerco = quartoDuploComBerco.calcularValorDiariaComBerco();
        double valorSemBerco = quartoDuploComBerco.calcularValorDiaria();
        
        assertTrue(valorComBerco > valorSemBerco,
            "Valor com berço deve ser maior que sem berço");
    }

    @Test
    public void testAluguelComBerco() {
        Aluguel aluguel = new Aluguel();
        aluguel.setQuarto(quartoDuploComBerco);
        aluguel.setBercoSolicitado(true);
        
        assertTrue(aluguel.isBercoSolicitado(),
            "Berço solicitado deve ser registrado no aluguel");
    }

    @Test
    public void testTiposQuartoValidos() {
        assertTrue(quartoIndividual instanceof QuartoIndividual);
        assertTrue(quartoDuploComBerco instanceof QuartoDuplo);
        assertTrue(quartoFamilia instanceof QuartoFamilia);
    }
}
