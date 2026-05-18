package com.hotelmarau.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historicos_hospedagem")
public class HistoricoHospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataRegistro = LocalDate.now();

    @OneToOne
    @JoinColumn(name = "residencia_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Residencia residencia;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "historico_id")
    private List<Aluguel> reservas = new ArrayList<>();

    public void registrar(Aluguel aluguel) {
        reservas.add(aluguel);
    }

    public List<Aluguel> listarPorResidencia(Long idResidencia) {
        return reservas.stream()
                .filter(a -> a.getResidencia().getId().equals(idResidencia))
                .toList();
    }
}
