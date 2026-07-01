package com.hotelmarau;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hotelmarau.model.Aluguel;
import com.hotelmarau.model.QuartoDuplo;
import com.hotelmarau.model.QuartoFamilia;
import com.hotelmarau.model.QuartoIndividual;

/**
 * Testes para cálculo de diárias e valores finais.
 * Cobertura: cada tipo de quarto, com e sem adicionais.
 */
public class DiariaCalculoTest {

    private QuartoIndividual quartoIndividual;
    private QuartoDuplo quartoDuploSemBerco;
    private QuartoDuplo quartoDuploComBerco;
    private QuartoFamilia quartoFamilia;

    @BeforeEach
    public void setup() {
        // Quarto Individual: R$100,00 (base)
        quartoIndividual = new QuartoIndividual(100.0, false, false, 1);

        // Quarto Duplo: R$150,00 (sem ar/hidro)
        quartoDuploSemBerco = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.QUEEN, false);

        // Quarto Duplo: R$150,00 com berço
        quartoDuploComBerco = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.QUEEN, true);

        // Quarto Família: R$200,00 base + adicionais + descontos
        quartoFamilia = new QuartoFamilia(200.0, false, false, 1, 1, 0, 2);
    }

    @Test
    public void testDiariaIndividualSemAdicionais() {
        double diaria = quartoIndividual.calcularValorDiaria();
        assertEquals(100.0, diaria, 0.01, 
            "Quarto Individual sem adicionais deve custar R$100.00");
    }

    @Test
    public void testDiariaDuploSemAdicionais() {
        double diaria = quartoDuploSemBerco.calcularValorDiaria();
        assertEquals(210.0, diaria, 0.01,
            "Quarto Duplo QUEEN sem adicionais comuns deve custar R$210.00 (150 + 60)");
    }

    @Test
    public void testDiariaDuploComAr() {
        QuartoDuplo duploComAr = new QuartoDuplo(150.0, true, false, QuartoDuplo.TipoCama.QUEEN, false);
        double diaria = duploComAr.calcularValorDiaria();
        assertEquals(240.0, diaria, 0.01,
            "Quarto Duplo QUEEN com ar deve custar R$240.00 (150 + 60 + 30)");
    }

    @Test
    public void testDiariaDuploComHidro() {
        QuartoDuplo duploComHidro = new QuartoDuplo(150.0, false, true, QuartoDuplo.TipoCama.QUEEN, false);
        double diaria = duploComHidro.calcularValorDiaria();
        assertEquals(260.0, diaria, 0.01,
            "Quarto Duplo QUEEN com hidro deve custar R$260.00 (150 + 60 + 50)");
    }

    @Test
    public void testDiariaDuploComArEHidro() {
        QuartoDuplo duploCompleto = new QuartoDuplo(150.0, true, true, QuartoDuplo.TipoCama.QUEEN, false);
        double diaria = duploCompleto.calcularValorDiaria();
        assertEquals(290.0, diaria, 0.01,
            "Quarto Duplo QUEEN com ar e hidro deve custar R$290.00 (150 + 60 + 30 + 50)");
    }

    @Test
    public void testDiariaDuploKingSemAdicionais() {
        QuartoDuplo king = new QuartoDuplo(150.0, false, false, QuartoDuplo.TipoCama.KING, false);
        assertEquals(250.0, king.calcularValorDiaria(), 0.01,
            "Quarto Duplo KING deve custar R$250.00 (150 + 100)");
    }

    @Test
    public void testValorTotalComDuasNoites() {
        Aluguel aluguel = new Aluguel();
        aluguel.setQuarto(quartoIndividual);
        aluguel.setNumeroHospedes(1);
        
        double valorDuasNoites = quartoIndividual.calcularValorTotal(2, 1);
        assertEquals(200.0, valorDuasNoites, 0.01,
            "2 noites em Quarto Individual devem custar R$200.00");
    }

    @Test
    public void testValorFamiliaComDescontoGrupo() {
        // Cálculo exato: 200 * (1 + 0.30) = 260; desconto de 10% => 234
        double valorTotal = quartoFamilia.calcularValorTotal(1, 6);
        assertEquals(234.0, valorTotal, 0.01,
            "Quarto Família com 6 hóspedes deve custar R$234.00 em 1 diária");
    }

    @Test
    public void testValorTotalAluguelIntegrado() {
        Aluguel aluguel = new Aluguel();
        aluguel.setQuarto(quartoDuploSemBerco);
        aluguel.setDataEntrada(java.time.LocalDateTime.of(2026, 1, 10, 12, 0));
        aluguel.setDataSaida(java.time.LocalDateTime.of(2026, 1, 12, 12, 0));
        aluguel.setNumeroHospedes(2);
        aluguel.setBercoSolicitado(false);
        
        double valorFinal = aluguel.calcularValorFinal();
        // 2 diárias * R$210.00 (Duplo QUEEN sem berço)
        assertEquals(420.0, valorFinal, 0.01,
            "Valor final deve ser R$420.00 para 2 diárias no quarto duplo QUEEN");
        assertEquals(aluguel.getValorFinal(), valorFinal, 0.01);

    }
}
