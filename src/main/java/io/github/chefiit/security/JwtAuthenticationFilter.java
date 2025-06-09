package io.github.chefiit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
        
        String bearer = req.getHeader("Authorization");
        
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            String username = jwtService.getUsernameFromToken(token);
            
            if (StringUtils.hasText(username) 
                && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                try {
                    // Carrega o UserDetails completo (CustomUserDetails)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtService.validateToken(token, userDetails)) {
                        // Usa as authorities do UserDetails ao invés de parsear do token
                        // Isso garante que temos as authorities mais atualizadas
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, // Principal agora é CustomUserDetails
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(req)
                        );
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (Exception e) {
                    // Log do erro se necessário, mas não quebra o filtro
                    logger.debug("Erro ao validar token JWT: " + e.getMessage());
                }
            }
        }
        
        chain.doFilter(req, res);
    }
}