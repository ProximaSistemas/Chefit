package io.github.chefiit.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.chefiit.controller.exception.ResourceNotFoundException;
import io.github.chefiit.model.Receita;
import io.github.chefiit.model.TipoUsuario;
import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.ReceitaRepository;
import io.github.chefiit.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final ReceitaRepository receitaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, 
                           ReceitaRepository receitaRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.receitaRepository = receitaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Usuario usuario) {
        try {
            // Verifica se email ou telefone já existem
            if (usuario.getEmail() != null && usuarioRepository.existsByEmail(usuario.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Email já está em uso!");
                return ResponseEntity.badRequest().body(error);
            }

            if (usuario.getTelefone() != null && usuarioRepository.existsByTelefone(usuario.getTelefone())) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Telefone já está em uso!");
                return ResponseEntity.badRequest().body(error);
            }

            // Configura dados do usuário
            usuario.setDataCadastro(LocalDateTime.now());
            
            // Criptografa a senha se fornecida
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
            
            // Define tipo padrão se não informado - apenas ADMIN pode criar outros tipos
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (usuario.getTipoUsuario() == null || !isAdmin(auth)) {
                usuario.setTipoUsuario(TipoUsuario.USER);
            }

            Usuario saved = usuarioRepository.save(usuario);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(saved);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao cadastrar usuário: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @PreAuthorize("@usuarioController.isOwnerOrAdmin(#id)")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @GetMapping("/{id}/metricas")
    @Operation(summary = "Calcular métricas do usuário")
    @PreAuthorize("@usuarioController.isOwnerOrAdmin(#id)")
    public ResponseEntity<MetricasUsuario> calcularMetricas(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        MetricasUsuario metricas = new MetricasUsuario(
                usuario.calcularIMC(),
                usuario.calcularBMR(),
                classificarIMC(usuario.calcularIMC()));

        return ResponseEntity.ok(metricas);
    }

    @PutMapping("/{id}/atualizar-metricas")
    @Operation(summary = "Atualizar métricas do usuário")
    @PreAuthorize("@usuarioController.isOwnerOrAdmin(#id)")
    public ResponseEntity<Usuario> atualizarMetricas(
            @PathVariable Long id,
            @RequestParam @Positive Double peso,
            @RequestParam(required = false) @Positive Double altura,
            @RequestParam(required = false) String objetivo) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuario.setPeso(peso);
        if (altura != null)
            usuario.setAltura(altura);
        if (objetivo != null)
            usuario.setObjetivo(objetivo);

        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @GetMapping("/{id}/plano-alimentar")
    @Operation(summary = "Gerar plano alimentar diário")
    @PreAuthorize("@usuarioController.isOwnerOrAdmin(#id)")
    public ResponseEntity<Map<String, List<Receita>>> gerarPlanoAlimentar(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        double caloriasDiarias = usuario.calcularBMR();
        Map<String, List<Receita>> plano = new HashMap<>();

        plano.put("cafeDaManha", receitaRepository.findRecomendadasByUsuario(
                usuario.getTipoDieta(), caloriasDiarias * 0.25));
        plano.put("almoco", receitaRepository.findRecomendadasByUsuario(
                usuario.getTipoDieta(), caloriasDiarias * 0.35));
        plano.put("jantar", receitaRepository.findRecomendadasByUsuario(
                usuario.getTipoDieta(), caloriasDiarias * 0.30));
        plano.put("lanches", receitaRepository.findRecomendadasByUsuario(
                usuario.getTipoDieta(), caloriasDiarias * 0.10));

        return ResponseEntity.ok(plano);
    }

    // Método helper para verificar se o usuário é dono do recurso ou admin
    public boolean isOwnerOrAdmin(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Verifica se é ADMIN
        if (isAdmin(auth)) {
            return true;
        }
        
        // Verifica se é o próprio usuário
        Usuario currentUser = usuarioRepository.findByEmail(username)
                .orElse(usuarioRepository.findByTelefone(username).orElse(null));
                
        return currentUser != null && currentUser.getId().equals(userId);
    }
    
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private String classificarIMC(double imc) {
        if (imc < 18.5)
            return "Abaixo do peso";
        if (imc < 24.9)
            return "Peso normal";
        if (imc < 29.9)
            return "Sobrepeso";
        return "Obesidade";
    }

    @Data
    @AllArgsConstructor
    static class MetricasUsuario {
        private final double imc;
        private final double bmr;
        private final String classificacaoImc;
    }
}