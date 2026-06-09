package com.agenda.controller;

import com.agenda.model.Compromisso;
import com.agenda.repository.CompromissoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compromissos")
@CrossOrigin(origins = "*")
public class CompromissoController {

    private final CompromissoRepository repository;

    public CompromissoController(CompromissoRepository repository) {
        this.repository = repository;
    }

    // CREATE - Criar novo compromisso
    @PostMapping
    public ResponseEntity<Compromisso> criar(@Valid @RequestBody Compromisso compromisso) {
        Compromisso salvo = repository.save(compromisso);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // READ - Listar todos os compromissos
    @GetMapping
    public ResponseEntity<List<Compromisso>> listar() {
        List<Compromisso> compromissos = repository.findAllByOrderByDataAscHoraAsc();
        return ResponseEntity.ok(compromissos);
    }

    // READ - Buscar compromisso por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    // UPDATE - Atualizar compromisso
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @Valid @RequestBody Compromisso dados) {
        return repository.findById(id)
                .map(comp -> {
                    comp.setTitulo(dados.getTitulo());
                    comp.setData(dados.getData());
                    comp.setHora(dados.getHora());
                    comp.setDescricao(dados.getDescricao());
                    comp.setContato(dados.getContato());
                    return ResponseEntity.ok(repository.save(comp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover compromisso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(comp -> {
                    repository.delete(comp);
                    return ResponseEntity.ok(Map.of("mensagem", "Compromisso removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
