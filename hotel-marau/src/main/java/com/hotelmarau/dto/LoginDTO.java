package com.hotelmarau.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    private String tipoAcesso;

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return this.senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoAcesso() { return this.tipoAcesso; }
    public void setTipoAcesso(String tipoAcesso) { this.tipoAcesso = tipoAcesso; }
}
