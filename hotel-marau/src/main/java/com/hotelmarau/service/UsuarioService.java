package com.hotelmarau.service;

import com.hotelmarau.dto.LoginDTO;
import com.hotelmarau.model.Usuario;
import com.hotelmarau.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Login simples: verifica email + senha no banco.
     * Retorna os dados do usuário se válido.
     */
    public Map<String, Object> login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!usuario.getSenha().equals(dto.getSenha())) {
            throw new RuntimeException("Senha incorreta.");
        }

        return Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "tipoAcesso", usuario.getTipoAcesso()
        );
    }

    public Usuario criar(String nome, String email, String senha, Usuario.TipoAcesso tipoAcesso) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipoAcesso(tipoAcesso);
        return usuarioRepository.save(usuario);
    }
}
