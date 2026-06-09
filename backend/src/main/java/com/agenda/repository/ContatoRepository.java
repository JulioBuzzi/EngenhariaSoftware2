package com.agenda.repository;

import com.agenda.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findAllByOrderByNomeAsc();

    List<Contato> findByNomeContainingIgnoreCase(String nome);
}
