package com.agenda;

import com.agenda.controller.AtendimentoController;
import com.agenda.exception.AtendimentoNotFoundException;
import com.agenda.model.Atendimento;
import com.agenda.model.ProfissionalSaude;
import com.agenda.model.enums.CategoriaEnum;
import com.agenda.model.enums.TipoReceitaEnum;
import com.agenda.service.AtendimentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES UNITÁRIOS - Atendimentos
 * Usa @WebMvcTest para testar o controller isoladamente.
 * O AtendimentoService é mockado com @MockBean.
 */
@WebMvcTest(AtendimentoController.class)
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtendimentoService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========== HELPERS ==========

    private ProfissionalSaude profissionalMock() {
        ProfissionalSaude p = new ProfissionalSaude();
        p.setId(1L);
        p.setNome("Dr. Carlos");
        p.setCategoria(CategoriaEnum.MEDICO);
        return p;
    }

    private Atendimento atendimentoMock() {
        Atendimento a = new Atendimento();
        a.setId(1L);
        a.setTitulo("Consulta de rotina");
        a.setData(LocalDate.of(2025, 1, 10));
        a.setHorario(LocalTime.of(9, 0));
        a.setProfissionalSaude(profissionalMock());
        a.setReceitas(List.of(TipoReceitaEnum.REMEDIO));
        return a;
    }

    // ========== CREATE ==========

    @Test
    void deveCriarAtendimentoComSucesso() throws Exception {
        Atendimento atendimento = atendimentoMock();

        when(service.criar(any(Atendimento.class))).thenReturn(atendimento);

        mockMvc.perform(post("/api/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atendimento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Consulta de rotina"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar400AoCriarAtendimentoSemCamposObrigatorios() throws Exception {
        // Atendimento sem título, data, horário e profissional
        String jsonInvalido = "{}";

        mockMvc.perform(post("/api/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    // ========== READ ==========

    @Test
    void deveBuscarAtendimentoPorId() throws Exception {
        Atendimento atendimento = atendimentoMock();

        when(service.buscarPorId(1L)).thenReturn(atendimento);

        mockMvc.perform(get("/api/atendimentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Consulta de rotina"))
                .andExpect(jsonPath("$.receitas[0]").value("REMEDIO"));
    }

    @Test
    void deveRetornar404ParaAtendimentoInexistente() throws Exception {
        when(service.buscarPorId(999L))
                .thenThrow(new AtendimentoNotFoundException(999L));

        mockMvc.perform(get("/api/atendimentos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarTodosAtendimentos() throws Exception {
        Atendimento a1 = atendimentoMock();

        Atendimento a2 = new Atendimento();
        a2.setId(2L);
        a2.setTitulo("Retorno");
        a2.setData(LocalDate.of(2025, 2, 5));
        a2.setHorario(LocalTime.of(10, 30));
        a2.setProfissionalSaude(profissionalMock());

        when(service.listarTodos()).thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/api/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Consulta de rotina"))
                .andExpect(jsonPath("$[1].titulo").value("Retorno"));
    }

    @Test
    void deveListarAtendimentosPorProfissional() throws Exception {
        Atendimento atendimento = atendimentoMock();

        when(service.listarPorProfissional(1L)).thenReturn(List.of(atendimento));

        mockMvc.perform(get("/api/atendimentos/profissional/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].profissionalSaude.nome").value("Dr. Carlos"));
    }

    @Test
    void deveRetornarListaVaziaParaProfissionalSemAtendimentos() throws Exception {
        when(service.listarPorProfissional(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/atendimentos/profissional/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ========== UPDATE ==========

    @Test
    void deveAtualizarAtendimentoComSucesso() throws Exception {
        Atendimento atualizado = atendimentoMock();
        atualizado.setTitulo("Consulta atualizada");
        atualizado.setLinkVideoconferencia("https://meet.google.com/abc");

        when(service.atualizar(eq(1L), any(Atendimento.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/atendimentos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Consulta atualizada"))
                .andExpect(jsonPath("$.linkVideoconferencia").value("https://meet.google.com/abc"));
    }

    @Test
    void deveRetornar404AoAtualizarAtendimentoInexistente() throws Exception {
        Atendimento dados = atendimentoMock();

        when(service.atualizar(eq(999L), any(Atendimento.class)))
                .thenThrow(new AtendimentoNotFoundException(999L));

        mockMvc.perform(put("/api/atendimentos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE ==========

    @Test
    void deveExcluirAtendimentoComSucesso() throws Exception {
        doNothing().when(service).excluir(1L);

        mockMvc.perform(delete("/api/atendimentos/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).excluir(1L);
    }

    @Test
    void deveRetornar404AoExcluirAtendimentoInexistente() throws Exception {
        doThrow(new AtendimentoNotFoundException(999L)).when(service).excluir(999L);

        mockMvc.perform(delete("/api/atendimentos/999"))
                .andExpect(status().isNotFound());
    }
}
