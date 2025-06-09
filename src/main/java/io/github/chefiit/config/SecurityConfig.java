package io.github.chefiit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.chefiit.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize e @PostAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/auth/**", "/public/**").permitAll()
                
                // Ingredientes - apenas leitura pública, criação apenas para ADMIN
                .requestMatchers("GET", "/ingredientes/**").permitAll()
                .requestMatchers("POST", "/ingredientes").hasRole("ADMIN")
                
                // Receitas - leitura pública, busca recomendadas e favoritas autenticadas
                .requestMatchers("GET", "/receitas/{id}").permitAll()
                .requestMatchers("GET", "/receitas/busca").permitAll()
                .requestMatchers("POST", "/receitas").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/receitas/recomendadas/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/receitas/**/favoritar/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/receitas/**/desfavoritar/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/receitas/favoritas/**").hasAnyRole("USER", "ADMIN")
                
                // Usuários - acesso restrito
                .requestMatchers("POST", "/usuarios").permitAll() // Cadastro público
                .requestMatchers("/usuarios/**").hasAnyRole("USER", "ADMIN")
                
                // Todo o resto requer autenticação
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}