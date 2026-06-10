package com.agenda;

import com.agenda.controller.ContatoController;
import com.agenda.model.Contato;
import com.agenda.repository.ContatoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES UNITÁRIOS - Contatos (DEV 1 - Ana)
 * Usa @WebMvcTest para testar apenas o controller isoladamente
 * O repository é mockado com @MockBean
 */
@WebMvcTest(ContatoController.class)
class ContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContatoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarContatoComSucesso() throws Exception {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");
        contato.setTelefone("31999999999");
        contato.setEmail("joao@email.com");

        when(repository.save(any(Contato.class))).thenReturn(contato);

        mockMvc.perform(post("/api/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void deveListarContatosVazio() throws Exception {
        when(repository.findAllByOrderByNomeAsc()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deveRetornar404ParaContatoInexistente() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contatos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarContatoComSucesso() throws Exception {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(repository.findById(1L)).thenReturn(Optional.of(contato));

        mockMvc.perform(delete("/api/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato removido com sucesso"));
    }
}
