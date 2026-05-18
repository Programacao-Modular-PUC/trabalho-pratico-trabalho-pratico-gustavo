package com.hotelmarau.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    private String email;

    // Getters e setters explícitos
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return this.cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return this.endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return this.telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
}
