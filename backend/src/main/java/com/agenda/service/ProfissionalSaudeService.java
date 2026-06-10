package com.agenda.service;

import com.agenda.model.ProfissionalSaude;
import com.agenda.repository.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository repository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository repository) {
        this.repository = repository;
    }

    public ProfissionalSaude criar(ProfissionalSaude profissional) {
        return repository.save(profissional);
    }

    public List<ProfissionalSaude> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    public Optional<ProfissionalSaude> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<ProfissionalSaude> atualizar(Long id, ProfissionalSaude dados) {
        return repository.findById(id).map(profissional -> {
            profissional.setNome(dados.getNome());
            profissional.setTelefone(dados.getTelefone());
            profissional.setEndereco(dados.getEndereco());
            profissional.setCategoria(dados.getCategoria());
            return repository.save(profissional);
        });
    }

    public boolean deletar(Long id) {
        return repository.findById(id).map(profissional -> {
            repository.delete(profissional);
            return true;
        }).orElse(false);
    }
}
