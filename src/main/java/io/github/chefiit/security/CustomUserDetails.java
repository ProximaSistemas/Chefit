package io.github.chefiit.security;

import io.github.chefiit.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    
    private final Usuario usuario;
    
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna as autoridades baseadas no tipo de usuário
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + usuario.getTipoUsuario().name())
        );
    }
    
    @Override
    public String getPassword() {
        return usuario.getSenha();
    }
    
    @Override
    public String getUsername() {
        // Retorna email como username principal, mas pode ser telefone também
        return usuario.getEmail() != null ? usuario.getEmail() : usuario.getTelefone();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // Método para acessar o usuário completo
    public Usuario getUsuario() {
        return usuario;
    }
}