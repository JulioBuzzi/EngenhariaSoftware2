package com.agenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "exame_laboratorio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExameLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição é obrigatória.")
    @Column(nullable = false)
    private String descricao;

    @NotBlank(message = "A posologia é obrigatória.")
    @Column(nullable = false)
    private String posologia;

    @NotNull(message = "O atendimento associado é obrigatório.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;
}