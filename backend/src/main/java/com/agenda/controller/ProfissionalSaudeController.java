package com.agenda.controller;

import com.agenda.model.ProfissionalSaude;
import com.agenda.service.ProfissionalSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profissionais-saude")
@CrossOrigin(origins = "*")
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService service;

    public ProfissionalSaudeController(ProfissionalSaudeService service) {
        this.service = service;
    }

    // CREATE - Cadastrar novo profissional
    @PostMapping
    public ResponseEntity<ProfissionalSaude> criar(@Valid @RequestBody ProfissionalSaude profissional) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(profissional));
    }

    // READ - Listar todos os profissionais
    @GetMapping
    public ResponseEntity<List<ProfissionalSaude>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // READ - Buscar profissional por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // UPDATE - Atualizar profissional
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @Valid @RequestBody ProfissionalSaude dados) {
        return service.atualizar(id, dados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover profissional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.ok(Map.of("mensagem", "Profissional removido com sucesso"));
        }
        return ResponseEntity.notFound().build();
    }
}
