package com.agenda.controller;

import com.agenda.model.ExameLaboratorio;
import com.agenda.service.ExameLaboratorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames-laboratorio")
@Tag(name = "Exame Laboratório", description = "Endpoints para gerenciamento de exames laboratoriais")
public class ExameLaboratorioController {

    private final ExameLaboratorioService service;

    public ExameLaboratorioController(ExameLaboratorioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Inserir um novo exame laboratorial", description = "Gera um novo registro de exame associado a um atendimento.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Exame criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    })
    public ResponseEntity<ExameLaboratorio> inserir(@Valid @RequestBody ExameLaboratorio exame) {
        ExameLaboratorio novoExame = service.inserir(exame);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoExame);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar um exame laboratorial existente", description = "Atualiza os dados de um exame com base no ID fornecido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exame atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    public ResponseEntity<ExameLaboratorio> alterar(@PathVariable Long id, @Valid @RequestBody ExameLaboratorio exame) {
        ExameLaboratorio exameAtualizado = service.alterar(id, exame);
        return ResponseEntity.ok(exameAtualizado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar exame por ID", description = "Retorna os detalhes de um exame laboratorial específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exame retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    public ResponseEntity<ExameLaboratorio> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Consultar todos os exames ou filtrar", description = "Retorna uma lista de exames. Pode ser filtrado por descrição ou posologia.")
    public ResponseEntity<List<ExameLaboratorio>> consultar(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String posologia) {
        
        if (descricao != null) {
            return ResponseEntity.ok(service.consultarPorDescricao(descricao));
        }
        if (posologia != null) {
            return ResponseEntity.ok(service.consultarPorPosologia(posologia));
        }
        return ResponseEntity.ok(service.consultarTodos());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um exame laboratorial", description = "Remove o registro do exame do sistema através do ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Exame excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
