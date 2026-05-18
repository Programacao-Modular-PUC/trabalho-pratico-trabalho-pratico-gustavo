package com.hotelmarau.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_quarto", discriminatorType = DiscriminatorType.STRING)
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double valorBase;
    private boolean possuiAr;
    private boolean possuiHidro;
    private boolean ativo = true;

    @Column(name = "tipo_quarto", insertable = false, updatable = false)
    private String tipoQuarto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Residencia residencia;

    @OneToMany(mappedBy = "quarto", fetch = FetchType.LAZY)
    private List<Aluguel> alugueis;

    // Adicional fixo de ar condicionado: R$30,00/diária
    protected static final double ADICIONAL_AR = 30.0;
    // Adicional fixo de hidromassagem: R$50,00/diária
    protected static final double ADICIONAL_HIDRO = 50.0;

    /**
     * Calcula o valor da diária conforme o tipo de quarto.
     * Cada subclasse implementa sua regra específica.
     */
    public abstract double calcularValorDiaria();

    /**
     * Calcula o valor total considerando diárias e número de hóspedes (quando aplicável).
     */
    public abstract double calcularValorTotal(int quantidadeDiarias, int numeroHospedes);

    /**
     * Verifica se o quarto está disponível no período informado.
     * Regra: Um quarto não pode ser alugado se já estiver ocupado no período.
     */
    public boolean verificarDisponibilidade(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        if (alugueis == null) return true;
        for (Aluguel a : alugueis) {
            if (a.getStatus() != null && a.getStatus() == com.hotelmarau.model.Aluguel.StatusAluguel.CANCELADO) continue;

            // Verifica sobreposição de períodos
            boolean sobrepoem = dataEntrada.isBefore(a.getDataSaida())
                    && dataSaida.isAfter(a.getDataEntrada());
            if (sobrepoem) return false;
        }
        return true;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    /**
     * Adicionais comuns: ar condicionado e hidromassagem
     */
    protected double calcularAdicionais() {
        double adicional = 0;
        if (possuiAr) adicional += ADICIONAL_AR;
        if (possuiHidro) adicional += ADICIONAL_HIDRO;
        return adicional;
    }

    // Getter explícito para `tipoQuarto` para evitar dependência de processamento Lombok em alguns ambientes
    public String getTipoQuarto() {
        return this.tipoQuarto;
    }

    // Getters/Setters explícitos mínimos para o IDE
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValorBase() {
        return this.valorBase;
    }

    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    public boolean isPossuiAr() {
        return this.possuiAr;
    }

    public void setPossuiAr(boolean possuiAr) {
        this.possuiAr = possuiAr;
    }

    public boolean isPossuiHidro() {
        return this.possuiHidro;
    }

    public void setPossuiHidro(boolean possuiHidro) {
        this.possuiHidro = possuiHidro;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Residencia getResidencia() {
        return this.residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }
}
