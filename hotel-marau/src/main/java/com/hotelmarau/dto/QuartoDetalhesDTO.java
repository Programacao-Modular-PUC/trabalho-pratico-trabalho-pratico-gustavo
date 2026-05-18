package com.hotelmarau.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartoDetalhesDTO {
    private Long id;

    // Tipo: INDIVIDUAL, DUPLO, FAMILIA
    private String tipo;

    private double valorBase;
    private boolean possuiAr;
    private boolean possuiHidro;

    private boolean ativo;

    // QuartoIndividual
    private Integer numeroCamas;

    // QuartoDuplo
    private QuartoDuploTipoCama tipoCama;
    private boolean temBerco;

    // QuartoFamilia
    private Integer camasSolteiro;
    private Integer camasCasal;
    private Integer camasQueenKing;
    private Integer capacidadeMaxima;
    private Integer numeroAmbientes;

    public enum QuartoDuploTipoCama {
        CASAL_COMUM, QUEEN, KING
    }

    // Getters e setters explícitos
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return this.tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValorBase() { return this.valorBase; }
    public void setValorBase(double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAr() { return this.possuiAr; }
    public void setPossuiAr(boolean possuiAr) { this.possuiAr = possuiAr; }

    public boolean isPossuiHidro() { return this.possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Integer getNumeroCamas() { return this.numeroCamas; }
    public void setNumeroCamas(Integer numeroCamas) { this.numeroCamas = numeroCamas; }

    public QuartoDuploTipoCama getTipoCama() { return this.tipoCama; }
    public void setTipoCama(QuartoDuploTipoCama tipoCama) { this.tipoCama = tipoCama; }

    public boolean isTemBerco() { return this.temBerco; }
    public void setTemBerco(boolean temBerco) { this.temBerco = temBerco; }

    public Integer getCamasSolteiro() { return this.camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public Integer getCamasCasal() { return this.camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return this.camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getCapacidadeMaxima() { return this.capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public Integer getNumeroAmbientes() { return this.numeroAmbientes; }
    public void setNumeroAmbientes(Integer numeroAmbientes) { this.numeroAmbientes = numeroAmbientes; }
}


