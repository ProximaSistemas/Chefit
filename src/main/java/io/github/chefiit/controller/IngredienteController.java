package io.github.chefiit.controller;

import java.net.URI;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.chefiit.model.Ingrediente;
import io.github.chefiit.repository.IngredienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ingredientes")
@Tag(name = "Ingredientes", description = "API para gerenciamento de ingredientes")
public class IngredienteController {
    
    private final IngredienteRepository ingredienteRepository;

    public IngredienteController(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo ingrediente", description = "Cadastra um novo ingrediente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingrediente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Ingrediente já existe")
    })
    public ResponseEntity<Ingrediente> cadastrar(@Valid @RequestBody Ingrediente ingrediente) {
        try {
            Ingrediente saved = ingredienteRepository.save(ingrediente);
            
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(saved);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ingrediente já existe");
        }
    }

    @GetMapping
    @Operation(summary = "Lista todos os ingredientes", description = "Retorna uma lista paginada de ingredientes ordenados por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredientes encontrados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum ingrediente encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<Ingrediente>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "nome") String sort) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
        Page<Ingrediente> resultado = ingredienteRepository.findAll(pageRequest);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca ingrediente por ID", description = "Retorna um ingrediente específico baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ingrediente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Ingrediente> buscarPorId(
            @Parameter(description = "ID do ingrediente", example = "1", required = true) @PathVariable Long id) {
        
        return ingredienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ingrediente não encontrado com ID: " + id));
    }
}