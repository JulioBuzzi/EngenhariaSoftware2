package com.agenda;

import com.agenda.model.Compromisso;
import com.agenda.model.Contato;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES DE INTEGRAÇÃO
 * Usa @SpringBootTest para carregar todo o contexto da aplicação
 * Testa a integração real entre Controller → Service → Repository → Banco
 * No CI, roda com PostgreSQL real via container
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveExecutarFluxoCompletoContato() throws Exception {
        // 1. CRIAR contato
        Contato contato = new Contato();
        contato.setNome("Maria Santos");
        contato.setTelefone("31988887777");
        contato.setEmail("maria@email.com");

        MvcResult result = mockMvc.perform(post("/api/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();

        // 2. BUSCAR contato criado
        mockMvc.perform(get("/api/contatos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        // 3. ATUALIZAR contato
        contato.setNome("Maria Santos Silva");
        contato.setEmail("maria.silva@email.com");

        mockMvc.perform(put("/api/contatos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Santos Silva"));

        // 4. DELETAR contato
        mockMvc.perform(delete("/api/contatos/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void deveVincularCompromissoAContato() throws Exception {
        // Criar contato
        Contato contato = new Contato();
        contato.setNome("Pedro Lima");
        contato.setTelefone("31977776666");

        MvcResult contatoResult = mockMvc.perform(post("/api/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isCreated())
                .andReturn();

        Long contatoId = objectMapper.readTree(
                contatoResult.getResponse().getContentAsString()).get("id").asLong();

        // Criar compromisso vinculado
        String compJson = String.format("""
            {
                "titulo": "Almoço de negócios",
                "data": "2024-12-20",
                "hora": "12:00",
                "contato": {"id": %d}
            }
            """, contatoId);

        mockMvc.perform(post("/api/compromissos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Almoço de negócios"));
    }
}
