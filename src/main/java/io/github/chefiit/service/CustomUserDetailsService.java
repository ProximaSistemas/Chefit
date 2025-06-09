package io.github.chefiit.service;

import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.UsuarioRepository;
import io.github.chefiit.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificacao) throws UsernameNotFoundException {
        Usuario usuario = null;
        
        // Tenta buscar por email primeiro
        if (identificacao.contains("@")) {
            usuario = usuarioRepository.findByEmail(identificacao).orElse(null);
        } else {
            // Se não tem @, tenta buscar por telefone
            usuario = usuarioRepository.findByTelefone(identificacao).orElse(null);
        }
        
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + identificacao);
        }
        
        // Retorna o CustomUserDetails ao invés do User padrão
        return new CustomUserDetails(usuario);
    }
}