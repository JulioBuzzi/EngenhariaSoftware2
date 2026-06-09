package com.agenda.service;

import com.agenda.exception.ResourceNotFoundException;
import com.agenda.model.ExameLaboratorio;
import com.agenda.repository.ExameLaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExameLaboratorioService {

    private final ExameLaboratorioRepository repository;

    public ExameLaboratorioService(ExameLaboratorioRepository repository) {
        this.repository = repository;
    }

    // Inserir(id) -> JPA cuida da geração se nulo, mas valida a persistência
    @Transactional
    public ExameLaboratorio inserir(ExameLaboratorio exame) {
        exame.setId(null); // Garante a operação de inserção pura
        return repository.save(exame);
    }

    // Alterar(id)
    @Transactional
    public ExameLaboratorio alterar(Long id, ExameLaboratorio exameAtualizado) {
        ExameLaboratorio exameExistente = consultarPorId(id);
        
        exameExistente.setDescricao(exameAtualizado.getDescricao());
        exameExistente.setPosologia(exameAtualizado.getPosologia());
        exameExistente.setAtendimento(exameAtualizado.getAtendimento());
        
        return repository.save(exameExistente);
    }

    // Consultar(id)
    @Transactional(readOnly = true)
    public ExameLaboratorio consultarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame Laboratorial não encontrado com o ID: " + id));
    }

    // Consultar Todos
    @Transactional(readOnly = true)
    public List<ExameLaboratorio> consultarTodos() {
        return repository.findAll();
    }

    // Consultar(descricao)
    @Transactional(readOnly = true)
    public List<ExameLaboratorio> consultarPorDescricao(String descricao) {
        return repository.findByDescricaoContainingIgnoreCase(descricao);
    }

    // Consultar(posologia)
    @Transactional(readOnly = true)
    public List<ExameLaboratorio> consultarPorPosologia(String posologia) {
        return repository.findByPosologiaContainingIgnoreCase(posologia);
    }

    // Excluir(id)
    @Transactional
    public void excluir(Long id) {
        ExameLaboratorio exame = consultarPorId(id);
        repository.delete(exame);
    }
}
