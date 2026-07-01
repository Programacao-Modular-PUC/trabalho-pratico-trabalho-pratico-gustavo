package com.hotelmarau.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Quarto Duplo (Casal)
 * Regras:
 *  - Voltado para casais
 *  - Possui cama casal COMUM ou QUEEN/KING
 *  - Pode ter berço (opcional, conforme solicitação do cliente)
 *  - Taxa extra se cliente solicitar berço: R$25,00/diária
 *  - Adicional por tipo de cama:
 *      CASAL_COMUM: R$0 adicional
 *      QUEEN:       R$60,00/diária
 *      KING:        R$100,00/diária
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "quartos_duplos")
@DiscriminatorValue("DUPLO")
public class QuartoDuplo extends Quarto {

    public enum TipoCama {
        CASAL_COMUM, QUEEN, KING
    }

    @Enumerated(EnumType.STRING)
    private TipoCama tipoCama;

    // O quarto PODE TER berço (estrutura física disponível)
    private boolean temBerco;

    // Adicional por tipo de cama
    private static final double ADICIONAL_QUEEN = 60.0;
    private static final double ADICIONAL_KING = 100.0;
    private static final double TAXA_BERCO = 25.0;

    public QuartoDuplo(double valorBase, boolean possuiAr, boolean possuiHidro,
                       TipoCama tipoCama, boolean temBerco) {
        this.setValorBase(valorBase);
        this.setPossuiAr(possuiAr);
        this.setPossuiHidro(possuiHidro);
        this.tipoCama = tipoCama;
        this.temBerco = temBerco;
    }

    /**
     * Calcula valor da diária sem berço:
     * valorBase + adicional da cama + adicionais comuns
     */
    @Override
    public double calcularValorDiaria() {
        return calcularValorDiariaSemBerco();
    }

    public double calcularValorDiariaSemBerco() {
        double valor = getValorBase() + calcularAdicionais();

        if (tipoCama == null) {
            throw new IllegalArgumentException("Tipo de cama deve ser informado para Quarto Duplo.");
        }

        switch (tipoCama) {
            case CASAL_COMUM -> {
                // Sem adicional de conforto
            }
            case QUEEN -> valor += ADICIONAL_QUEEN;
            case KING -> valor += ADICIONAL_KING;
        }

        return valor;
    }

    /**
     * Calcula valor da diária com berço (se solicitado pelo cliente)
     */
    public double calcularValorDiariaComBerco() {
        if (!temBerco) {
            throw new IllegalStateException("Este quarto não possui berço disponível.");
        }
        // Compatível com testes: taxa de berço R$25/diária
        return calcularValorDiariaSemBerco() + TAXA_BERCO;
    }

    @Override
    public double calcularValorTotal(int quantidadeDiarias, int numeroHospedes) {
        return calcularValorDiaria() * quantidadeDiarias;
    }

    /**
     * Calcula valor total com berço solicitado
     */
    public double calcularValorTotalComBerco(int quantidadeDiarias) {
        return calcularValorDiariaComBerco() * quantidadeDiarias;
    }

    // Getter explícito para o campo `temBerco`
    public boolean isTemBerco() {
        return this.temBerco;
    }

    // Setters explícitos
    public void setTipoCama(TipoCama tipoCama) {
        this.tipoCama = tipoCama;
    }

    public void setTemBerco(boolean temBerco) {
        this.temBerco = temBerco;
    }
}
