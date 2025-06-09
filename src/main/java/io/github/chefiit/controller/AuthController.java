package io.github.chefiit.controller;

import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.UsuarioRepository;
import io.github.chefiit.security.CustomUserDetails;
import io.github.chefiit.security.JwtService;
import io.github.chefiit.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String identificacao = loginData.get("identificacao");
            String senha = loginData.get("senha");

            if (identificacao == null || senha == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Identificação e senha são obrigatórios");
                return ResponseEntity.badRequest().body(error);
            }

            // Autentica com email ou telefone
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identificacao, senha)
            );

            // Extrai o CustomUserDetails do authentication
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Usuario usuario = userDetails.getUsuario();

            // Converte authorities para CSV string
            String authoritiesCsv = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

            // Gera o token JWT usando o email como subject
            String email = usuario.getEmail() != null ? usuario.getEmail() : usuario.getTelefone();
            String token = jwtService.generateToken(email, authoritiesCsv);

            // Monta a resposta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("telefone", usuario.getTelefone());
            response.put("tipoUsuario", usuario.getTipoUsuario().name());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Credenciais inválidas");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", false);
                response.put("erro", "Token não fornecido ou formato inválido");
                return ResponseEntity.badRequest().body(response);
            }

            String token = authHeader.substring(7);
            String username = jwtService.getUsernameFromToken(token);
            
            // Busca o usuário para validar se ainda existe
            Usuario usuario;
            if (username.contains("@")) {
                usuario = usuarioRepository.findByEmail(username).orElse(null);
            } else {
                usuario = usuarioRepository.findByTelefone(username).orElse(null);
            }

            if (usuario == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", false);
                response.put("erro", "Usuário não encontrado");
                return ResponseEntity.badRequest().body(response);
            }

            // Cria UserDetails para validação
            CustomUserDetails userDetails = new CustomUserDetails(usuario);
            
            if (jwtService.validateToken(token, userDetails)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", true);
                response.put("usuario", username);
                response.put("userId", usuario.getId());
                response.put("nome", usuario.getNome());
                response.put("tipoUsuario", usuario.getTipoUsuario().name());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", false);
                response.put("erro", "Token expirado ou inválido");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valido", false);
            response.put("erro", "Erro ao validar token");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Usuario usuario = authorizationService.getCurrentUser();
            
            if (usuario == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Usuário não encontrado");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("telefone", usuario.getTelefone());
            response.put("tipoUsuario", usuario.getTipoUsuario().name());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar dados do usuário");
            return ResponseEntity.badRequest().body(error);
        }
    }
}