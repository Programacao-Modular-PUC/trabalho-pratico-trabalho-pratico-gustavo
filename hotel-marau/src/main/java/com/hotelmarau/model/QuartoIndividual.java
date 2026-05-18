package com.hotelmarau.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Quarto Individual (Solteiro)
 * Regras:
 *  - Pode ter 1 ou mais camas de solteiro
 *  - Não permite berço
 *  - Valor base + adicional por cama (somente se mais de 1 cama)
 *  - Adicional por cama extra: R$40,00
 *  - Limite de hóspedes = número de camas
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "quartos_individuais")
@DiscriminatorValue("INDIVIDUAL")
public class QuartoIndividual extends Quarto {

    private int numeroCamas;

    // Adicional por cama extra (a partir da 2ª cama)
    private static final double ADICIONAL_POR_CAMA_EXTRA = 40.0;

    public QuartoIndividual(double valorBase, boolean possuiAr, boolean possuiHidro, int numeroCamas) {
        this.setValorBase(valorBase);
        this.setPossuiAr(possuiAr);
        this.setPossuiHidro(possuiHidro);
        this.numeroCamas = numeroCamas;
    }

    /**
     * Calcula o valor da diária:
     * - 1 cama: valorBase + adicionais
     * - 2+ camas: valorBase + (camas - 1) * ADICIONAL_POR_CAMA_EXTRA + adicionais
     */
    @Override
    public double calcularValorDiaria() {
        double valor = getValorBase() + calcularAdicionais();
        if (numeroCamas > 1) {
            valor += (numeroCamas - 1) * ADICIONAL_POR_CAMA_EXTRA;
        }
        return valor;
    }

    @Override
    public double calcularValorTotal(int quantidadeDiarias, int numeroHospedes) {
        return calcularValorDiaria() * quantidadeDiarias;
    }

    /**
     * Limite de hóspedes = número de camas
     */
    public int getLimiteHospedes() {
        return numeroCamas;
    }

    // Getter/Setter explícitos
    public int getNumeroCamas() { return this.numeroCamas; }
    public void setNumeroCamas(int numeroCamas) { this.numeroCamas = numeroCamas; }
}
