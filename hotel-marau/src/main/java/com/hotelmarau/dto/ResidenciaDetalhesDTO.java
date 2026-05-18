package com.hotelmarau.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidenciaDetalhesDTO {
    private Long id;

    private String endereco;
    private String numero;
    private String bairro;
    private String cep;
    private String telefone;
    private String email;

    private List<QuartoDetalhesDTO> quartos = new ArrayList<>();

    // Getters e setters explícitos
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

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

    public List<QuartoDetalhesDTO> getQuartos() { return this.quartos; }
    public void setQuartos(List<QuartoDetalhesDTO> quartos) { this.quartos = quartos; }
}


