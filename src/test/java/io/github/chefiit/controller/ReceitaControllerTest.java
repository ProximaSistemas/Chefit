package io.github.chefiit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.chefiit.config.TestConfig;
import io.github.chefiit.model.Receita;
import io.github.chefiit.model.Usuario;
import io.github.chefiit.repository.ReceitaRepository;
import io.github.chefiit.repository.UsuarioRepository;


@WebMvcTest(ReceitaController.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class ReceitaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ReceitaRepository receitaRepository;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar uma nova receita com sucesso")
    void testCadastrarReceita() throws Exception {
        Receita receita = getReceitaTest();
        when(receitaRepository.save(any(Receita.class))).thenReturn(receita);

        mockMvc.perform(post("/receitas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receita)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Bolo de Cenoura"))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("Deve buscar receita por ID com sucesso")
    void testBuscarPorId() throws Exception {
        when(receitaRepository.findByIdWithIngredientes(1L)).thenReturn(Optional.of(getReceitaTest()));

        mockMvc.perform(get("/receitas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bolo de Cenoura"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar receita inexistente")
    void testBuscarPorIdNaoEncontrado() throws Exception {
        when(receitaRepository.findByIdWithIngredientes(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/receitas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar receitas recomendadas com sucesso")
    void testBuscarRecomendadas() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setTipoDieta("PADRAO");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(receitaRepository.findRecomendadasByUsuario(anyString(), anyDouble()))
                .thenReturn(List.of(getReceitaTest()));

        mockMvc.perform(get("/receitas/recomendadas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bolo de Cenoura"));
    }

    @Test
    @DisplayName("Deve buscar receitas com filtros com sucesso")
    void testBuscarComFiltros() throws Exception {
        when(receitaRepository.findByFiltros(anyString(), any(), any(), any()))
                .thenReturn(List.of(getReceitaTest()));

        mockMvc.perform(get("/receitas/busca")
                .param("categoria", "DOCES")
                .param("tempoPreparoMaximo", "30")
                .param("caloriasMaximas", "300")
                .param("proteinasMinimas", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bolo de Cenoura"));
    }

    @Test
    @DisplayName("Deve favoritar receita com sucesso")
    void testFavoritar() throws Exception {
        Usuario usuario = new Usuario();
        Receita receita = getReceitaTest();
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        mockMvc.perform(post("/receitas/1/favoritar/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve desfavoritar receita com sucesso")
    void testDesfavoritar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.getFavoritas().add(getReceitaTest());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(delete("/receitas/1/desfavoritar/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve listar favoritas com sucesso")
    void testListarFavoritas() throws Exception {
        when(receitaRepository.findFavoritasByUsuarioId(1L))
                .thenReturn(List.of(getReceitaTest()));

        mockMvc.perform(get("/receitas/favoritas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Bolo de Cenoura"));
    }

    private Receita getReceitaTest() {
        Receita receita = new Receita();
        receita.setId(1L);
        receita.setNome("Bolo de Cenoura");
        receita.setDescricao("Bolo caseiro de cenoura");
        receita.setTempoPreparo(45);
        receita.setPorcoes(8);
        receita.setModoPreparo("1. Bater no liquidificador...");
        receita.setCategoria("DOCES");
        receita.setCaloriasPorcao(250.0);
        receita.setDataCadastro(LocalDateTime.now());
        receita.setIngredientes(new ArrayList<>());
        receita.setFavoritadaPor(new HashSet<>());
        return receita;
    }
}