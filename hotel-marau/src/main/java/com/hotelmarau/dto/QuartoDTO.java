package com.hotelmarau.dto;

import com.hotelmarau.model.QuartoDuplo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuartoDTO {

    // Tipo: INDIVIDUAL, DUPLO, FAMILIA
    @NotNull(message = "Tipo do quarto é obrigatório")
    private String tipo;

    @NotNull(message = "Valor base é obrigatório")
    private Double valorBase;

    private boolean possuiAr;
    private boolean possuiHidro;

    // QuartoIndividual
    private Integer numeroCamas;

    // QuartoDuplo
    private QuartoDuplo.TipoCama tipoCama;
    private boolean temBerco;

    // QuartoFamilia
    private Integer camasSolteiro;
    private Integer camasCasal;
    private Integer camasQueenKing;
    private Integer numeroAmbientes;

    // Getters e setters explícitos para compatibilidade com IDE
    public String getTipo() { return this.tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValorBase() { return this.valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAr() { return this.possuiAr; }
    public void setPossuiAr(boolean possuiAr) { this.possuiAr = possuiAr; }

    public boolean isPossuiHidro() { return this.possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Integer getNumeroCamas() { return this.numeroCamas; }
    public void setNumeroCamas(Integer numeroCamas) { this.numeroCamas = numeroCamas; }

    public QuartoDuplo.TipoCama getTipoCama() { return this.tipoCama; }
    public void setTipoCama(QuartoDuplo.TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public boolean isTemBerco() { return this.temBerco; }
    public void setTemBerco(boolean temBerco) { this.temBerco = temBerco; }

    public Integer getCamasSolteiro() { return this.camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public Integer getCamasCasal() { return this.camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return this.camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getNumeroAmbientes() { return this.numeroAmbientes; }
    public void setNumeroAmbientes(Integer numeroAmbientes) { this.numeroAmbientes = numeroAmbientes; }
}
