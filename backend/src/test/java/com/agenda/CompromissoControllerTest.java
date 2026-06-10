package com.agenda;

import com.agenda.controller.CompromissoController;
import com.agenda.model.Compromisso;
import com.agenda.repository.CompromissoRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES UNITÁRIOS - Compromissos (DEV 2 - Bruno)
 * Usa @WebMvcTest para testar apenas o controller isoladamente
 */
@WebMvcTest(CompromissoController.class)
class CompromissoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompromissoRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveCriarCompromissoComSucesso() throws Exception {
        Compromisso comp = new Compromisso();
        comp.setId(1L);
        comp.setTitulo("Reunião com cliente");
        comp.setData(LocalDate.of(2024, 12, 15));
        comp.setHora(LocalTime.of(14, 0));

        when(repository.save(any(Compromisso.class))).thenReturn(comp);

        mockMvc.perform(post("/api/compromissos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Reunião com cliente"));
    }

    @Test
    void deveListarCompromissosOrdenados() throws Exception {
        Compromisso comp1 = new Compromisso();
        comp1.setId(1L);
        comp1.setTitulo("Reunião manhã");
        comp1.setData(LocalDate.of(2024, 12, 15));

        Compromisso comp2 = new Compromisso();
        comp2.setId(2L);
        comp2.setTitulo("Almoço");
        comp2.setData(LocalDate.of(2024, 12, 15));

        when(repository.findAllByOrderByDataAscHoraAsc())
                .thenReturn(Arrays.asList(comp1, comp2));

        mockMvc.perform(get("/api/compromissos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Reunião manhã"))
                .andExpect(jsonPath("$[1].titulo").value("Almoço"));
    }

    @Test
    void deveRetornar404ParaCompromissoInexistente() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/compromissos/999"))
                .andExpect(status().isNotFound());
    }
}
