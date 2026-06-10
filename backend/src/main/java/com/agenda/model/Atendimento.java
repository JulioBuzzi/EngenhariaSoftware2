package com.agenda.model;

import com.agenda.model.ProfissionalSaude;
import com.agenda.model.enums.TipoReceitaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "atendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    private LocalDate data;

    @NotNull(message = "Horário é obrigatório")
    @Column(nullable = false)
    private LocalTime horario;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @Column(name = "link_videoconferencia")
    private String linkVideoconferencia;

    @ElementCollection
    @CollectionTable(
            name = "atendimento_receita",
            joinColumns = @JoinColumn(name = "atendimento_id")
    )
    @Column(name = "receita")
    @Enumerated(EnumType.STRING)
    private List<TipoReceitaEnum> receitas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_saude_id", nullable = false)
    private ProfissionalSaude profissionalSaude;
}