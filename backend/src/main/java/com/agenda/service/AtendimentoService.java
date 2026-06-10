package com.agenda.service;

import com.agenda.model.Atendimento;
import com.agenda.repository.AtendimentoRepository;
import com.agenda.repository.ProfissionalSaudeRepository;
import com.agenda.exception.AtendimentoNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public Atendimento buscarPorId(Long id) {
        return atendimentoRepository.findById(id)
                .orElseThrow(() -> new AtendimentoNotFoundException(id));
    }

    public List<Atendimento> listarTodos() {
        return atendimentoRepository.findAll();
    }

    public List<Atendimento> listarPorProfissional(Long profissionalId) {
        return atendimentoRepository.findByProfissionalSaudeId(profissionalId);
    }

    @Transactional
    public Atendimento criar(Atendimento atendimento) {
        validarProfissional(atendimento);
        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public Atendimento atualizar(Long id, Atendimento dados) {
        Atendimento atendimento = buscarPorId(id);
        validarProfissional(dados);

        atendimento.setData(dados.getData());
        atendimento.setHorario(dados.getHorario());
        atendimento.setTitulo(dados.getTitulo());
        atendimento.setLinkVideoconferencia(dados.getLinkVideoconferencia());
        atendimento.setReceitas(dados.getReceitas());
        atendimento.setProfissionalSaude(dados.getProfissionalSaude());

        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void excluir(Long id) {
        if (!atendimentoRepository.existsById(id)) {
            throw new AtendimentoNotFoundException(id);
        }
        atendimentoRepository.deleteById(id);
    }

    private void validarProfissional(Atendimento atendimento) {
        Long profissionalId = atendimento.getProfissionalSaude().getId();
        profissionalSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException(
                        "Profissional não encontrado com ID: " + profissionalId));
    }
}