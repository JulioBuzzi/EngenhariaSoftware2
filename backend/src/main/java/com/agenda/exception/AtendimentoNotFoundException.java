package com.agenda.exception;

public class AtendimentoNotFoundException extends RuntimeException {

    public AtendimentoNotFoundException(Long id) {
        super("Atendimento não encontrado com ID: " + id);
    }
}