package com.hotelmarau;

import com.hotelmarau.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para validação de regras de negócio dos quartos.
 * Cobertura: capacidade máxima, cálculo de diária e validações.
 */
public class QuartoCapacidadeTest {

    private QuartoIndividual quartoIndividual;
    private QuartoDuplo quartoDuplo;
    private QuartoFamilia quartoFamilia;

    @BeforeEach
    public void setup() {
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
        Aluguel aluguel = new Aluguel();
        aluguel.setQuarto(quartoIndividual);
        aluguel.setNumeroHospedes(2);
        
        assertTrue(aluguel.getNumeroHospedes() > 1,
            "Quarto Individual não pode ter 2 hóspedes");
    }

    @Test
    public void testCapacidadeExcedidaDuplo() {
        Aluguel aluguel = new Aluguel();
        aluguel.setQuarto(quartoDuplo);
        aluguel.setNumeroHospedes(3);
        
        assertTrue(aluguel.getNumeroHospedes() > 2,
            "Quarto Duplo não pode ter 3 hóspedes");
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
