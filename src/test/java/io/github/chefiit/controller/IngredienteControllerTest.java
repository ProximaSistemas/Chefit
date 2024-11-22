package io.github.chefiit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.chefiit.config.TestConfig;
import io.github.chefiit.model.Ingrediente;
import io.github.chefiit.repository.IngredienteRepository;


@WebMvcTest(IngredienteController.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class IngredienteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar um novo ingrediente com sucesso")
    void testCadastrarIngrediente() throws Exception {
        Ingrediente ingrediente = getIngredienteTest();
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        mockMvc.perform(post("/ingredientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingrediente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Farinha de Trigo"))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("Deve listar ingredientes com sucesso")
    void testListarIngredientes() throws Exception {
        List<Ingrediente> ingredientes = List.of(getIngredienteTest());
        Page<Ingrediente> page = new PageImpl<>(ingredientes);
        when(ingredienteRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/ingredientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Farinha de Trigo"));
    }

    @Test
    @DisplayName("Deve retornar 204 quando não houver ingredientes")
    void testListarIngredientesVazio() throws Exception {
        Page<Ingrediente> emptyPage = new PageImpl<>(List.of());
        when(ingredienteRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/ingredientes"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar ingrediente por ID com sucesso")
    void testBuscarPorId() throws Exception {
        Ingrediente ingrediente = getIngredienteTest();
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));

        mockMvc.perform(get("/ingredientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Farinha de Trigo"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando ingrediente não existir")
    void testBuscarPorIdNaoEncontrado() throws Exception {
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ingredientes/1"))
                .andExpect(status().isNotFound());
    }

    private Ingrediente getIngredienteTest() {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNome("Farinha de Trigo");
        ingrediente.setUnidadeMedida("KG");
        return ingrediente;
    }
}