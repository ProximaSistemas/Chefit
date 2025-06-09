package io.github.chefiit.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.UsuarioRepository;
import io.github.chefiit.security.CustomUserDetails;

@Service
public class AuthorizationService {

    private final UsuarioRepository usuarioRepository;

    public AuthorizationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Verifica se o usuário atual é o dono do recurso ou é admin
     */
    public boolean isOwnerOrAdmin(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // Verifica se é ADMIN
        if (hasRole(auth, "ADMIN")) {
            return true;
        }

        // Verifica se é o próprio usuário
        Usuario currentUser = getCurrentUser();
        return currentUser != null && currentUser.getId().equals(userId);
    }

    /**
     * Verifica se o usuário atual é admin
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return hasRole(auth, "ADMIN");
    }

    /**
     * Verifica se o usuário atual é o próprio usuário ou admin
     */
    public boolean canAccessUserResource(Long userId) {
        return isOwnerOrAdmin(userId);
    }

    /**
     * Obtém o usuário atual logado
     * Primeiro tenta pelo principal (quando há CustomUserDetails),
     * senão busca no banco pelo username
     */
    public Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // Se o principal é CustomUserDetails, pega diretamente o Usuario
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUsuario();
        }

        // Fallback: busca pelo username no banco
        String username = auth.getName();
        return usuarioRepository.findByEmail(username)
                .orElse(usuarioRepository.findByTelefone(username).orElse(null));
    }

    /**
     * Verifica se pode favoritar/desfavoritar receita
     */
    public boolean canManageFavorites(Long userId) {
        return isOwnerOrAdmin(userId);
    }

    /**
     * Obtém o ID do usuário atual
     */
    public Long getCurrentUserId() {
        Usuario currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }

    // Métodos privados auxiliares
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> 
                    grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
}