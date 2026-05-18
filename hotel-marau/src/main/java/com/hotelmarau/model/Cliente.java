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
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Column(unique = true)
    private String cpf;

    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    private String email;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Aluguel> alugueis = new ArrayList<>();

    public void atualizarDados(String nome, String endereco, String telefone, String email) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (endereco != null) this.endereco = endereco;
        if (telefone != null && !telefone.isBlank()) this.telefone = telefone;
        if (email != null && !email.isBlank()) this.email = email;
    }

    public List<Aluguel> listarReservas() {
        return alugueis;
    }
}
