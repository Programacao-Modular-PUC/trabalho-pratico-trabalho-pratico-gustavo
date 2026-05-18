package com.hotelmarau.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "residencias")
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    private String email;

    @OneToMany(mappedBy = "residencia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Quarto> quartos = new ArrayList<>();

    @OneToOne(mappedBy = "residencia", cascade = CascadeType.ALL)
    private HistoricoHospedagem historicoHospedagem;

    public void adicionarQuarto(Quarto quarto) {
        quartos.add(quarto);
        quarto.setResidencia(this);
    }

    public void removerQuarto(Long idQuarto) {
        quartos.removeIf(q -> q.getId().equals(idQuarto));
    }

    public List<Quarto> listarQuartos() {
        return quartos;
    }

    // Getter/setter explícitos para a lista de quartos
    public List<Quarto> getQuartos() { return this.quartos; }
    public void setQuartos(List<Quarto> quartos) { this.quartos = quartos; }

    // Getter explícito mínimo para `id` (ajuda diagnósticos em IDEs sem Lombok)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
