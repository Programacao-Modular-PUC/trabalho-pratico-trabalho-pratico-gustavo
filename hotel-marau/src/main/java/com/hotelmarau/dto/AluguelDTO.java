package com.hotelmarau.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AluguelDTO {
    @NotNull(message = "ID da residência é obrigatório")
    private Long residenciaId;

    @NotNull(message = "ID do quarto é obrigatório")
    private Long quartoId;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Data de entrada é obrigatória")
    private LocalDateTime dataEntrada;

    @NotNull(message = "Data de saída é obrigatória")
    private LocalDateTime dataSaida;

    private int numeroHospedes = 1;

    // Para QuartoDuplo: informa se o cliente deseja berço
    private boolean bercoSolicitado = false;

    // Getters e setters explícitos
    public Long getResidenciaId() { return this.residenciaId; }
    public void setResidenciaId(Long residenciaId) { this.residenciaId = residenciaId; }

    public Long getQuartoId() { return this.quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public Long getClienteId() { return this.clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDateTime getDataEntrada() { return this.dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return this.dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public int getNumeroHospedes() { return this.numeroHospedes; }
    public void setNumeroHospedes(int numeroHospedes) { this.numeroHospedes = numeroHospedes; }

    public boolean isBercoSolicitado() { return this.bercoSolicitado; }
    public void setBercoSolicitado(boolean bercoSolicitado) { this.bercoSolicitado = bercoSolicitado; }
}
