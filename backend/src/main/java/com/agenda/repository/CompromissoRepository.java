package com.agenda.repository;

import com.agenda.model.Compromisso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompromissoRepository extends JpaRepository<Compromisso, Long> {

    List<Compromisso> findAllByOrderByDataAscHoraAsc();

    List<Compromisso> findByDataOrderByHoraAsc(LocalDate data);

    List<Compromisso> findByContatoId(Long contatoId);
}
