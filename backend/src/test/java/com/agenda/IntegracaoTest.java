package com.agenda;

import com.fasterxml.jackson.databind.JsonNode;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES DE INTEGRAÇÃO
 * Usa @SpringBootTest para carregar todo o contexto da aplicação.
 * Testa a integração real entre Controller → Service → Repository → Banco.
 * Roda com H2 em memória via perfil "test" (application-test.properties).
 *
 * Cobre os 5 domínios do backend:
 *   - Contato
 *   - Compromisso
 *   - ProfissionalSaude
 *   - Atendimento
 *   - ExameLaboratorio
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

    // =========================================================
    // UTILIDADES
    // =========================================================

    private Long extrairId(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    // =========================================================
    // 1. CONTATOS
    // =========================================================

    @Test
    void deveExecutarFluxoCompletoContato() throws Exception {
        // CREATE
        String contatoJson = """
                {
                    "nome": "Maria Santos",
                    "telefone": "31988887777",
                    "email": "maria@email.com",
                    "endereco": "Rua X, 10"
                }
                """;

        MvcResult criadoResult = mockMvc.perform(post("/api/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contatoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andReturn();

        Long id = extrairId(criadoResult);

        // READ by ID
        mockMvc.perform(get("/api/contatos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        // READ list
        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // UPDATE
        String contatoAtualizado = """
                {
                    "nome": "Maria Santos Silva",
                    "telefone": "31988887777",
                    "email": "maria.silva@email.com"
                }
                """;

        mockMvc.perform(put("/api/contatos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contatoAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Santos Silva"));

        // DELETE
        mockMvc.perform(delete("/api/contatos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato removido com sucesso"));

        // Confirma 404 após deleção
        mockMvc.perform(get("/api/contatos/" + id))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // 2. COMPROMISSOS
    // =========================================================

    @Test
    void deveVincularCompromissoAContato() throws Exception {
        // Cria contato
        String contatoJson = """
                {"nome": "Pedro Lima", "telefone": "31977776666"}
                """;

        MvcResult contatoResult = mockMvc.perform(post("/api/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contatoJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long contatoId = extrairId(contatoResult);

        // Cria compromisso vinculado
        String compJson = String.format("""
                {
                    "titulo": "Almoço de negócios",
                    "data": "2025-12-20",
                    "hora": "12:00",
                    "contato": {"id": %d}
                }
                """, contatoId);

        MvcResult compResult = mockMvc.perform(post("/api/compromissos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Almoço de negócios"))
                .andReturn();

        Long compId = extrairId(compResult);

        // READ
        mockMvc.perform(get("/api/compromissos/" + compId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contato.id").value(contatoId));

        // UPDATE
        String compAtualizado = String.format("""
                {
                    "titulo": "Jantar de negócios",
                    "data": "2025-12-20",
                    "hora": "19:00",
                    "contato": {"id": %d}
                }
                """, contatoId);

        mockMvc.perform(put("/api/compromissos/" + compId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(compAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Jantar de negócios"));

        // DELETE
        mockMvc.perform(delete("/api/compromissos/" + compId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Compromisso removido com sucesso"));
    }

    // =========================================================
    // 3. PROFISSIONAIS DE SAÚDE
    // =========================================================

    @Test
    void deveExecutarFluxoCompletoProfissionalSaude() throws Exception {
        // CREATE
        String profJson = """
                {
                    "nome": "Dra. Beatriz Carvalho",
                    "telefone": "31966665555",
                    "endereco": "Av. Saúde, 200 - BH",
                    "categoria": "PSICOLOGO"
                }
                """;

        MvcResult criadoResult = mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Dra. Beatriz Carvalho"))
                .andExpect(jsonPath("$.categoria").value("PSICOLOGO"))
                .andReturn();

        Long id = extrairId(criadoResult);

        // READ by ID
        mockMvc.perform(get("/api/profissionais-saude/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefone").value("31966665555"));

        // READ list
        mockMvc.perform(get("/api/profissionais-saude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // UPDATE
        String profAtualizado = """
                {
                    "nome": "Dra. Beatriz Carvalho Silva",
                    "telefone": "31966665555",
                    "endereco": "Av. Saúde, 300 - BH",
                    "categoria": "PSICOLOGO"
                }
                """;

        mockMvc.perform(put("/api/profissionais-saude/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(profAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dra. Beatriz Carvalho Silva"));

        // DELETE
        mockMvc.perform(delete("/api/profissionais-saude/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Profissional removido com sucesso"));

        // Confirma 404 após deleção
        mockMvc.perform(get("/api/profissionais-saude/" + id))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // 4. ATENDIMENTOS
    // =========================================================

    @Test
    void deveExecutarFluxoCompletoAtendimento() throws Exception {
        // Pré-requisito: criar profissional
        String profJson = """
                {
                    "nome": "Dr. Roberto Mendes",
                    "telefone": "31955554444",
                    "categoria": "MEDICO"
                }
                """;

        MvcResult profResult = mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long profId = extrairId(profResult);

        // CREATE atendimento
        String atendJson = String.format("""
                {
                    "titulo": "Consulta inicial",
                    "data": "2025-04-10",
                    "horario": "09:00:00",
                    "receitas": ["REMEDIO"],
                    "profissionalSaude": {"id": %d}
                }
                """, profId);

        MvcResult atendResult = mockMvc.perform(post("/api/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atendJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Consulta inicial"))
                .andReturn();

        Long atendId = extrairId(atendResult);

        // READ by ID
        mockMvc.perform(get("/api/atendimentos/" + atendId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profissionalSaude.id").value(profId))
                .andExpect(jsonPath("$.receitas[0]").value("REMEDIO"));

        // READ all
        mockMvc.perform(get("/api/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // READ by profissional
        mockMvc.perform(get("/api/atendimentos/profissional/" + profId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Consulta inicial"));

        // UPDATE
        String atendAtualizado = String.format("""
                {
                    "titulo": "Consulta de retorno",
                    "data": "2025-05-10",
                    "horario": "10:00:00",
                    "linkVideoconferencia": "https://meet.google.com/xyz",
                    "receitas": ["REMEDIO", "ATIVIDADE_FISICA"],
                    "profissionalSaude": {"id": %d}
                }
                """, profId);

        mockMvc.perform(put("/api/atendimentos/" + atendId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(atendAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Consulta de retorno"))
                .andExpect(jsonPath("$.linkVideoconferencia").value("https://meet.google.com/xyz"));

        // DELETE
        mockMvc.perform(delete("/api/atendimentos/" + atendId))
                .andExpect(status().isNoContent());

        // Confirma 404 após deleção
        mockMvc.perform(get("/api/atendimentos/" + atendId))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // 5. EXAMES LABORATORIAIS
    // =========================================================

    @Test
    void deveExecutarFluxoCompletoExameLaboratorio() throws Exception {
        // Pré-requisito: criar profissional
        String profJson = """
                {
                    "nome": "Dr. Fábio Torres",
                    "categoria": "MEDICO"
                }
                """;

        MvcResult profResult = mockMvc.perform(post("/api/profissionais-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long profId = extrairId(profResult);

        // Pré-requisito: criar atendimento
        String atendJson = String.format("""
                {
                    "titulo": "Consulta para exames",
                    "data": "2025-06-01",
                    "horario": "08:00:00",
                    "profissionalSaude": {"id": %d}
                }
                """, profId);

        MvcResult atendResult = mockMvc.perform(post("/api/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atendJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long atendId = extrairId(atendResult);

        // CREATE exame
        String exameJson = String.format("""
                {
                    "descricao": "Hemograma completo",
                    "posologia": "Jejum de 8 horas",
                    "atendimento": {"id": %d}
                }
                """, atendId);

        MvcResult exameResult = mockMvc.perform(post("/api/exames-laboratorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exameJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Hemograma completo"))
                .andReturn();

        Long exameId = extrairId(exameResult);

        // READ by ID
        mockMvc.perform(get("/api/exames-laboratorio/" + exameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posologia").value("Jejum de 8 horas"))
                .andExpect(jsonPath("$.atendimento.id").value(atendId));

        // READ all
        mockMvc.perform(get("/api/exames-laboratorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // READ filtrado por descricao
        mockMvc.perform(get("/api/exames-laboratorio").param("descricao", "Hemograma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Hemograma completo"));

        // READ filtrado por posologia
        mockMvc.perform(get("/api/exames-laboratorio").param("posologia", "Jejum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posologia").value("Jejum de 8 horas"));

        // UPDATE
        String exameAtualizado = String.format("""
                {
                    "descricao": "Hemograma completo + Ferritina",
                    "posologia": "Jejum de 12 horas",
                    "atendimento": {"id": %d}
                }
                """, atendId);

        mockMvc.perform(put("/api/exames-laboratorio/" + exameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(exameAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Hemograma completo + Ferritina"))
                .andExpect(jsonPath("$.posologia").value("Jejum de 12 horas"));

        // DELETE
        mockMvc.perform(delete("/api/exames-laboratorio/" + exameId))
                .andExpect(status().isNoContent());

        // Confirma 404 após deleção
        mockMvc.perform(get("/api/exames-laboratorio/" + exameId))
                .andExpect(status().isNotFound());
    }
}
