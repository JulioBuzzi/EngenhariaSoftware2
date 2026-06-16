package com.agenda;

import com.agenda.controller.ExameLaboratorioController;
import com.agenda.exception.ResourceNotFoundException;
import com.agenda.model.Atendimento;
import com.agenda.model.ExameLaboratorio;
import com.agenda.model.ProfissionalSaude;
import com.agenda.model.enums.CategoriaEnum;
import com.agenda.service.ExameLaboratorioService;
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
 * TESTES UNITÁRIOS - Exames Laboratoriais
 * Usa @WebMvcTest para testar o controller isoladamente.
 * O ExameLaboratorioService é mockado com @MockBean.
 */
@WebMvcTest(ExameLaboratorioController.class)
class ExameLaboratorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExameLaboratorioService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========== HELPERS ==========

    private Atendimento atendimentoMock() {
        ProfissionalSaude profissional = new ProfissionalSaude();
        profissional.setId(1L);
        profissional.setNome("Dra. Fernanda");
        profissional.setCategoria(CategoriaEnum.MEDICO);

        Atendimento atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setTitulo("Consulta geral");
        atendimento.setData(LocalDate.of(2025, 3, 15));
        atendimento.setHorario(LocalTime.of(8, 0));
        atendimento.setProfissionalSaude(profissional);
        return atendimento;
    }

    private ExameLaboratorio exameMock() {
        ExameLaboratorio exame = new ExameLaboratorio();
        exame.setId(1L);
        exame.setDescricao("Hemograma completo");
        exame.setPosologia("Jejum de 8 horas");
        exame.setAtendimento(atendimentoMock());
        return exame;
    }

    // ========== CREATE (inserir) ==========

    @Test
    void deveInserirExameComSucesso() throws Exception {
        ExameLaboratorio exame = exameMock();

        when(service.inserir(any(ExameLaboratorio.class))).thenReturn(exame);

        mockMvc.perform(post("/api/exames-laboratorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Hemograma completo"))
                .andExpect(jsonPath("$.posologia").value("Jejum de 8 horas"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar400AoInserirExameSemDescricao() throws Exception {
        ExameLaboratorio exame = exameMock();
        exame.setDescricao(null);

        mockMvc.perform(post("/api/exames-laboratorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoInserirExameSemPosologia() throws Exception {
        ExameLaboratorio exame = exameMock();
        exame.setPosologia(null);

        mockMvc.perform(post("/api/exames-laboratorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoInserirExameSemAtendimento() throws Exception {
        ExameLaboratorio exame = exameMock();
        exame.setAtendimento(null);

        mockMvc.perform(post("/api/exames-laboratorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isBadRequest());
    }

    // ========== READ (consultar) ==========

    @Test
    void deveConsultarExamePorId() throws Exception {
        when(service.consultarPorId(1L)).thenReturn(exameMock());

        mockMvc.perform(get("/api/exames-laboratorio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Hemograma completo"))
                .andExpect(jsonPath("$.atendimento.titulo").value("Consulta geral"));
    }

    @Test
    void deveRetornar404ParaExameInexistente() throws Exception {
        when(service.consultarPorId(999L))
                .thenThrow(new ResourceNotFoundException("Exame Laboratorial não encontrado com o ID: 999"));

        mockMvc.perform(get("/api/exames-laboratorio/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveConsultarTodosExames() throws Exception {
        ExameLaboratorio e1 = exameMock();

        ExameLaboratorio e2 = new ExameLaboratorio();
        e2.setId(2L);
        e2.setDescricao("Glicemia em jejum");
        e2.setPosologia("Jejum de 12 horas");
        e2.setAtendimento(atendimentoMock());

        when(service.consultarTodos()).thenReturn(Arrays.asList(e1, e2));

        mockMvc.perform(get("/api/exames-laboratorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Hemograma completo"))
                .andExpect(jsonPath("$[1].descricao").value("Glicemia em jejum"));
    }

    @Test
    void deveConsultarExamesPorDescricao() throws Exception {
        when(service.consultarPorDescricao("Hemograma")).thenReturn(List.of(exameMock()));

        mockMvc.perform(get("/api/exames-laboratorio").param("descricao", "Hemograma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Hemograma completo"));
    }

    @Test
    void deveConsultarExamesPorPosologia() throws Exception {
        when(service.consultarPorPosologia("Jejum")).thenReturn(List.of(exameMock()));

        mockMvc.perform(get("/api/exames-laboratorio").param("posologia", "Jejum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].posologia").value("Jejum de 8 horas"));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverExames() throws Exception {
        when(service.consultarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/exames-laboratorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ========== UPDATE (alterar) ==========

    @Test
    void deveAlterarExameComSucesso() throws Exception {
        ExameLaboratorio atualizado = exameMock();
        atualizado.setDescricao("Hemograma completo + Plaquetas");
        atualizado.setPosologia("Jejum de 12 horas");

        when(service.alterar(eq(1L), any(ExameLaboratorio.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/exames-laboratorio/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Hemograma completo + Plaquetas"))
                .andExpect(jsonPath("$.posologia").value("Jejum de 12 horas"));
    }

    @Test
    void deveRetornar404AoAlterarExameInexistente() throws Exception {
        ExameLaboratorio dados = exameMock();

        when(service.alterar(eq(999L), any(ExameLaboratorio.class)))
                .thenThrow(new ResourceNotFoundException("Exame Laboratorial não encontrado com o ID: 999"));

        mockMvc.perform(put("/api/exames-laboratorio/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE (excluir) ==========

    @Test
    void deveExcluirExameComSucesso() throws Exception {
        doNothing().when(service).excluir(1L);

        mockMvc.perform(delete("/api/exames-laboratorio/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).excluir(1L);
    }

    @Test
    void deveRetornar404AoExcluirExameInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Exame Laboratorial não encontrado com o ID: 999"))
                .when(service).excluir(999L);

        mockMvc.perform(delete("/api/exames-laboratorio/999"))
                .andExpect(status().isNotFound());
    }
}
