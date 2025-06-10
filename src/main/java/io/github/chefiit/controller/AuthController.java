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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização de usuários")
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
    @Operation(
        summary = "Realizar login",
        description = "Autentica um usuário usando email/telefone e senha, retornando um token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "userId": 1,
                        "nome": "João Silva",
                        "email": "joao@email.com",
                        "telefone": "11999999999",
                        "tipoUsuario": "USER"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Credenciais inválidas ou dados obrigatórios não fornecidos",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Campos obrigatórios",
                        value = """
                        {
                            "erro": "Identificação e senha são obrigatórios"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Credenciais inválidas",
                        value = """
                        {
                            "erro": "Credenciais inválidas"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<?> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados de login do usuário",
            required = true,
            content = @Content(
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "identificacao": "joao@email.com",
                        "senha": "minhasenha123"
                    }
                    """
                )
            )
        )
        @RequestBody Map<String, String> loginData) {
        
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
    @Operation(
        summary = "Validar token JWT",
        description = "Valida se um token JWT é válido e retorna informações do usuário autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token válido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenValidationResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "valido": true,
                        "usuario": "joao@email.com",
                        "userId": 1,
                        "nome": "João Silva",
                        "tipoUsuario": "USER"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token inválido ou não fornecido",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Token não fornecido",
                        value = """
                        {
                            "valido": false,
                            "erro": "Token não fornecido ou formato inválido"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Token expirado",
                        value = """
                        {
                            "valido": false,
                            "erro": "Token expirado ou inválido"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Usuário não encontrado",
                        value = """
                        {
                            "valido": false,
                            "erro": "Usuário não encontrado"
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<?> validateToken(
        @Parameter(
            description = "Token JWT no formato 'Bearer {token}'",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @RequestHeader("Authorization") String authHeader) {
        
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
    @Operation(
        summary = "Obter dados do usuário atual",
        description = "Retorna os dados do usuário autenticado com base no token JWT"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário retornados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CurrentUserResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "userId": 1,
                        "nome": "João Silva",
                        "email": "joao@email.com",
                        "telefone": "11999999999",
                        "tipoUsuario": "USER"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar dados do usuário",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Usuário não encontrado",
                        value = """
                        {
                            "erro": "Usuário não encontrado"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Erro genérico",
                        value = """
                        {
                            "erro": "Erro ao buscar dados do usuário"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token não fornecido ou inválido"
        )
    })
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

    // DTOs para documentação
    @Schema(description = "Dados para realizar login")
    public static class LoginRequest {
        @Schema(description = "Email ou telefone do usuário", example = "joao@email.com")
        public String identificacao;
        
        @Schema(description = "Senha do usuário", example = "minhasenha123")
        public String senha;
    }

    @Schema(description = "Resposta do login bem-sucedido")
    public static class LoginResponse {
        @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        public String token;
        
        @Schema(description = "ID do usuário", example = "1")
        public Long userId;
        
        @Schema(description = "Nome do usuário", example = "João Silva")
        public String nome;
        
        @Schema(description = "Email do usuário", example = "joao@email.com")
        public String email;
        
        @Schema(description = "Telefone do usuário", example = "11999999999")
        public String telefone;
        
        @Schema(description = "Tipo do usuário", example = "USER")
        public String tipoUsuario;
    }

    @Schema(description = "Resposta da validação do token")
    public static class TokenValidationResponse {
        @Schema(description = "Indica se o token é válido", example = "true")
        public Boolean valido;
        
        @Schema(description = "Email ou telefone do usuário", example = "joao@email.com")
        public String usuario;
        
        @Schema(description = "ID do usuário", example = "1")
        public Long userId;
        
        @Schema(description = "Nome do usuário", example = "João Silva")
        public String nome;
        
        @Schema(description = "Tipo do usuário", example = "USER")
        public String tipoUsuario;
        
        @Schema(description = "Mensagem de erro (quando token inválido)", example = "Token expirado ou inválido")
        public String erro;
    }

    @Schema(description = "Dados do usuário atual")
    public static class CurrentUserResponse {
        @Schema(description = "ID do usuário", example = "1")
        public Long userId;
        
        @Schema(description = "Nome do usuário", example = "João Silva")
        public String nome;
        
        @Schema(description = "Email do usuário", example = "joao@email.com")
        public String email;
        
        @Schema(description = "Telefone do usuário", example = "11999999999")
        public String telefone;
        
        @Schema(description = "Tipo do usuário", example = "USER")
        public String tipoUsuario;
    }
}