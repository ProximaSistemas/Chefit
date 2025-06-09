package io.github.chefiit.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.chefiit.controller.exception.ResourceNotFoundException;
import io.github.chefiit.model.Receita;
import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.ReceitaRepository;
import io.github.chefiit.repository.UsuarioRepository;
import io.github.chefiit.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/receitas")
@Tag(name = "Receitas", description = "API para gerenciamento de receitas")
public class ReceitaController {
    private final ReceitaRepository receitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthorizationService authorizationService;

    public ReceitaController(ReceitaRepository receitaRepository, 
                           UsuarioRepository usuarioRepository,
                           AuthorizationService authorizationService) {
        this.receitaRepository = receitaRepository;
        this.usuarioRepository = usuarioRepository;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova receita", description = "Cadastra uma receita com seus ingredientes e informações nutricionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receita cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Receita> cadastrar(@RequestBody @Valid Receita receita) {
        // TODO: Se futuramente adicionar campo 'autor' no modelo Receita,
        // descomente as linhas abaixo para associar ao usuário atual
        /*
        if (!authorizationService.isAdmin()) {
            Usuario currentUser = authorizationService.getCurrentUser();
            if (currentUser != null) {
                receita.setAutor(currentUser);
            }
        }
        */
        
        receita.setDataCadastro(LocalDateTime.now());
        Receita receitaSalva = receitaRepository.save(receita);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(receitaSalva.getId())
                .toUri();

        return ResponseEntity.created(location).body(receitaSalva);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma receita pelo ID")
    // Endpoint público - qualquer um pode ver receitas
    public ResponseEntity<Receita> buscarPorId(@PathVariable Long id) {
        return receitaRepository.findByIdWithIngredientes(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Receita não encontrada"));
    }

    @GetMapping("/recomendadas/{usuarioId}")
    @Operation(summary = "Busca receitas recomendadas para o usuário")
    @PreAuthorize("@authorizationService.canAccessUserResource(#usuarioId)")
    public ResponseEntity<List<Receita>> buscarRecomendadas(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        double caloriasRefeicao = usuario.calcularBMR() / 3;
        
        List<Receita> receitas = receitaRepository
                .findRecomendadasByUsuario(usuario.getTipoDieta(), caloriasRefeicao);

        return receitas.isEmpty() ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.ok(receitas);
    }

    @GetMapping("/busca")
    @Operation(summary = "Busca receitas com filtros")
    // Endpoint público - qualquer um pode buscar receitas
    public ResponseEntity<List<Receita>> buscarComFiltros(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer tempoPreparoMaximo,
            @RequestParam(required = false) Integer caloriasMaximas,
            @RequestParam(required = false) Double proteinasMinimas) {

        List<Receita> receitas = receitaRepository
                .findByFiltros(categoria, tempoPreparoMaximo, caloriasMaximas, proteinasMinimas);

        return receitas.isEmpty() ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.ok(receitas);
    }

    @PostMapping("/{receitaId}/favoritar/{usuarioId}")
    @Operation(summary = "Adiciona uma receita aos favoritos do usuário")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.canManageFavorites(#usuarioId)")
    public void favoritar(@PathVariable Long receitaId, @PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                
        Receita receita = receitaRepository.findById(receitaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receita não encontrada"));

        usuario.getFavoritas().add(receita);
        usuarioRepository.save(usuario);
    }

    @DeleteMapping("/{receitaId}/desfavoritar/{usuarioId}")
    @Operation(summary = "Remove uma receita dos favoritos do usuário")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.canManageFavorites(#usuarioId)")
    public void desfavoritar(@PathVariable Long receitaId, @PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuario.getFavoritas().removeIf(r -> r.getId().equals(receitaId));
        usuarioRepository.save(usuario);
    }

    @GetMapping("/favoritas/{usuarioId}")
    @Operation(summary = "Lista as receitas favoritas do usuário")
    @PreAuthorize("@authorizationService.canAccessUserResource(#usuarioId)")
    public ResponseEntity<List<Receita>> listarFavoritas(@PathVariable Long usuarioId) {
        List<Receita> favoritas = receitaRepository.findFavoritasByUsuarioId(usuarioId);
        return favoritas.isEmpty() ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.ok(favoritas);
    }
}