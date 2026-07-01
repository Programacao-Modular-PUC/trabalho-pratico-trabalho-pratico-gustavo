package com.hotelmarau.model;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aluguel (Reserva)
 * Regras de negócio:
 *  - Diárias sempre iniciam às 12h
 *  - Entrada após 12h → conta como diária completa
 *  - Saída após 12h → adiciona nova diária
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id", nullable = false)
    private Residencia residencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;

    private int quantidadeDiarias;
    private double valorFinal;
    private int numeroHospedes;

    // Berço solicitado (apenas para QuartoDuplo)
    private boolean bercoSolicitado = false;

    @Enumerated(EnumType.STRING)
    private StatusAluguel status = StatusAluguel.CONFIRMADO;

    public enum StatusAluguel {
        CONFIRMADO, CANCELADO, CONCLUIDO
    }

    /**
     * Calcula o número de diárias considerando as regras:
     *  - Hora base: 12h
     *  - Entrada após 12h → conta como diária completa
     *  - Saída após 12h → adiciona nova diária
     */
    public int calcularDiarias() {
        if (dataEntrada == null || dataSaida == null) return 0;

        LocalDateTime checkInBase = dataEntrada.toLocalDate().atTime(12, 0);
        LocalDateTime checkOutBase = dataSaida.toLocalDate().atTime(12, 0);

        // Número de dias entre as datas base
        long diasBase = Duration.between(checkInBase, checkOutBase).toDays();

        // Entrada após 12h → conta como diária completa (já está incluído nos dias base)
        // Saída após 12h → adiciona 1 diária extra
        int extra = dataSaida.getHour() > 12 ? 1 : 0;

        // Mínimo de 1 diária
        long total = Math.max(diasBase + extra, 1);
        return (int) total;
    }

    /**
     * Calcula o valor final do aluguel conforme o tipo de quarto.
     */
    public double calcularValorFinal() {
        this.quantidadeDiarias = calcularDiarias();

        if (quarto instanceof QuartoDuplo duplo && bercoSolicitado) {
            this.valorFinal = duplo.calcularValorDiariaComBerco() * quantidadeDiarias;
        } else if (quarto instanceof QuartoFamilia familia) {
            this.valorFinal = familia.calcularValorTotal(quantidadeDiarias, numeroHospedes);
        } else {
            this.valorFinal = quarto.calcularValorTotal(quantidadeDiarias, numeroHospedes);
        }

        return valorFinal;
    }

    public void confirmarReserva() {
        this.status = StatusAluguel.CONFIRMADO;
    }

    public void cancelarReserva() {
        this.status = StatusAluguel.CANCELADO;
    }

    /**
     * Gera o recibo formatado do aluguel
     */
    public String gerarRecibo() {
        return String.format("""
                Data e horário de entrada: %s
                Data e horário de saída: %s
                Número de diárias: %d
                Total à pagar: R$ %.2f
                """,
                dataEntrada,
                dataSaida,
                quantidadeDiarias,
                valorFinal
        );
    }

    // Getters explícitos para compatibilidade caso o processamento de anotações (Lombok)
    // não gere os métodos durante a compilação no ambiente do usuário.
    public LocalDateTime getDataEntrada() {
        return this.dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return this.dataSaida;
    }

    public StatusAluguel getStatus() {
        return this.status;
    }

    // Setters explícitos
    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public void setNumeroHospedes(int numeroHospedes) {
        this.numeroHospedes = numeroHospedes;
    }

    public void setBercoSolicitado(boolean bercoSolicitado) {
        this.bercoSolicitado = bercoSolicitado;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }
}
