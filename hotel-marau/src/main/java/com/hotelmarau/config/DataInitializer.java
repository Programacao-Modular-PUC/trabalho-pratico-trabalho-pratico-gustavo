package com.hotelmarau.config;

import com.hotelmarau.model.Usuario;
import com.hotelmarau.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        // Cria usuário administrador padrão se não existir
        if (usuarioRepository.findByEmail("admin@hotelmarau.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@hotelmarau.com");
            admin.setSenha("123456");
            admin.setTipoAcesso(Usuario.TipoAcesso.PROPRIETARIO);
            usuarioRepository.save(admin);
            log.info("✅ Usuário padrão criado: admin@hotelmarau.com / 123456");
        }
    }
}
