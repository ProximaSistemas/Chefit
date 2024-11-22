package io.github.chefiit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

@WebMvcTest(UsuarioController.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @MockBean
    private ReceitaRepository receitaRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void testCadastrarUsuario() throws Exception {
        Usuario usuario = getUsuarioTest();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void testBuscarPorId() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(getUsuarioTest()));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void testBuscarPorIdNaoEncontrado() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve calcular métricas do usuário corretamente")
    void testCalcularMetricas() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(getUsuarioTest()));

        mockMvc.perform(get("/usuarios/1/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imc", closeTo(24.49, 0.01)))
                .andExpect(jsonPath("$.classificacaoImc").value("Peso normal"))
                .andExpect(jsonPath("$.bmr").isNumber());
    }

    @Test
    @DisplayName("Deve atualizar métricas do usuário com sucesso")
    void testAtualizarMetricas() throws Exception {
        Usuario usuario = getUsuarioTest();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/1/atualizar-metricas")
                .param("peso", "80.0")
                .param("altura", "1.80")
                .param("objetivo", "GANHAR_MASSA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peso").value(80.0))
                .andExpect(jsonPath("$.altura").value(1.80))
                .andExpect(jsonPath("$.objetivo").value("GANHAR_MASSA"));
    }

    @Test
    @DisplayName("Deve gerar plano alimentar com sucesso")
    void testGerarPlanoAlimentar() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(getUsuarioTest()));
        when(receitaRepository.findRecomendadasByUsuario(anyString(), anyDouble()))
                .thenReturn(List.of(new Receita()));

        mockMvc.perform(get("/usuarios/1/plano-alimentar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cafeDaManha").exists())
                .andExpect(jsonPath("$.almoco").exists())
                .andExpect(jsonPath("$.jantar").exists())
                .andExpect(jsonPath("$.lanches").exists());
    }

    private Usuario getUsuarioTest() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setIdade(30);
        usuario.setPeso(75.0);
        usuario.setAltura(1.75);
        usuario.setSexo('M');
        usuario.setObjetivo("MANTER_PESO");
        usuario.setTipoDieta("PADRAO");
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setFavoritas(new HashSet<>());
        return usuario;
    }
}