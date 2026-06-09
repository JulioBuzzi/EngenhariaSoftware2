package com.agenda.repository;

import com.agenda.model.enums.CategoriaEnum;
import com.agenda.model.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    List<ProfissionalSaude> findAllByOrderByNomeAsc();

    List<ProfissionalSaude> findByCategoria(CategoriaEnum categoria);

    List<ProfissionalSaude> findByNomeContainingIgnoreCase(String nome);
}
