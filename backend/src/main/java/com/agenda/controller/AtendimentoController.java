package com.agenda.controller;

import com.agenda.model.Atendimento;
import com.agenda.service.AtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService service;

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Atendimento>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<Atendimento>> listarPorProfissional(
            @PathVariable Long profissionalId) {
        return ResponseEntity.ok(service.listarPorProfissional(profissionalId));
    }

    @PostMapping
    public ResponseEntity<Atendimento> criar(@Valid @RequestBody Atendimento atendimento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(atendimento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendimento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Atendimento atendimento) {
        return ResponseEntity.ok(service.atualizar(id, atendimento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}