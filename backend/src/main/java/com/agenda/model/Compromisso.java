package com.agenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "compromissos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compromisso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Column(length = 200, nullable = false)
    private String titulo;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    private LocalTime hora;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contato_id")
    private Contato contato;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();
}
