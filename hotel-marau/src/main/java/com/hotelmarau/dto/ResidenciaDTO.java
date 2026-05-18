package com.hotelmarau.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResidenciaDTO {
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

    // Getters e setters explícitos
    public String getEndereco() { return this.endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return this.numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return this.bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCep() { return this.cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getTelefone() { return this.telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
}
