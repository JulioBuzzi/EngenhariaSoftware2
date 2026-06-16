package com.agenda;

import com.agenda.controller.ProfissionalSaudeController;
import com.agenda.model.ProfissionalSaude;
import com.agenda.model.enums.CategoriaEnum;
import com.agenda.service.ProfissionalSaudeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES UNITÁRIOS - Profissionais de Saúde
 * Usa @WebMvcTest para testar o controller isoladamente.
 * O ProfissionalSaudeService é mockado com @MockBean.
 */
@WebMvcTest(ProfissionalSaudeController.class)
class ProfissionalSaudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfissionalSaudeService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    // ========== HELPERS ==========

    private ProfissionalSaude profissionalMock() {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(1L);
        p.setNome("Dr. Carlos Oliveira");
        p.setTelefone("31988881111");
        p.setEndereco("Rua das Flores, 100 - BH");
        p.setCategoria(CategoriaEnum.MEDICO);
        return p;
    }

    // ========== CREATE ==========

    @Test
    void deveCriarProfissionalComSucesso() throws Exception {
        ProfissionalSaude profissional = profissionalMock();

        when(service.criar(any(ProfissionalSaude.class))).thenReturn(profissional);

        mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Dr. Carlos Oliveira"))
                .andExpect(jsonPath("$.categoria").value("MEDICO"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveCriarProfissionalComCategoriaPsicologo() throws Exception {
        ProfissionalSaude profissional = profissionalMock();
        profissional.setNome("Dra. Ana Lima");
        profissional.setCategoria(CategoriaEnum.PSICOLOGO);

        when(service.criar(any(ProfissionalSaude.class))).thenReturn(profissional);

        mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria").value("PSICOLOGO"));
    }

    @Test
    void deveRetornar400AoCriarProfissionalSemNome() throws Exception {
        ProfissionalSaude profissional = profissionalMock();
        profissional.setNome(null);

        mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoCriarProfissionalSemCategoria() throws Exception {
        ProfissionalSaude profissional = profissionalMock();
        profissional.setCategoria(null);

        mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isBadRequest());
    }

    // ========== READ ==========

    @Test
    void deveBuscarProfissionalPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(profissionalMock()));

        mockMvc.perform(get("/api/profissionais-saude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dr. Carlos Oliveira"))
                .andExpect(jsonPath("$.telefone").value("31988881111"));
    }

    @Test
    void deveRetornar404ParaProfissionalInexistente() throws Exception {
        when(service.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profissionais-saude/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarProfissionaisOrdenadosPorNome() throws Exception {
        ProfissionalSaude p1 = profissionalMock();

        ProfissionalSaude p2 = new ProfissionalSaude();
        p2.setId(2L);
        p2.setNome("Dra. Beatriz Santos");
        p2.setCategoria(CategoriaEnum.FISIOTERAPEUTA);

        // Service.listar() já retorna ordenado por nome
        when(service.listar()).thenReturn(Arrays.asList(p2, p1));

        mockMvc.perform(get("/api/profissionais-saude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Dra. Beatriz Santos"))
                .andExpect(jsonPath("$[1].nome").value("Dr. Carlos Oliveira"));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverProfissionais() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/profissionais-saude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ========== UPDATE ==========

    @Test
    void deveAtualizarProfissionalComSucesso() throws Exception {
        ProfissionalSaude atualizado = profissionalMock();
        atualizado.setNome("Dr. Carlos Oliveira Jr.");
        atualizado.setTelefone("31900002222");

        when(service.atualizar(eq(1L), any(ProfissionalSaude.class)))
                .thenReturn(Optional.of(atualizado));

        mockMvc.perform(put("/api/profissionais-saude/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dr. Carlos Oliveira Jr."))
                .andExpect(jsonPath("$.telefone").value("31900002222"));
    }

    @Test
    void deveRetornar404AoAtualizarProfissionalInexistente() throws Exception {
        ProfissionalSaude dados = profissionalMock();

        when(service.atualizar(eq(999L), any(ProfissionalSaude.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/profissionais-saude/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE ==========

    @Test
    void deveDeletarProfissionalComSucesso() throws Exception {
        when(service.deletar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/profissionais-saude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Profissional removido com sucesso"));
    }

    @Test
    void deveRetornar404AoDeletarProfissionalInexistente() throws Exception {
        when(service.deletar(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/profissionais-saude/999"))
                .andExpect(status().isNotFound());
    }
}
