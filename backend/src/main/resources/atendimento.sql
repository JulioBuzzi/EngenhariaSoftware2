-- Tabela principal
CREATE TABLE atendimento (
                             id                    BIGSERIAL PRIMARY KEY,
                             data                  DATE          NOT NULL,
                             horario               TIME          NOT NULL,
                             titulo                VARCHAR(255)  NOT NULL,
                             link_videoconferencia VARCHAR(500),
                             profissional_saude_id BIGINT        NOT NULL,

                             CONSTRAINT fk_atendimento_profissional
                                 FOREIGN KEY (profissional_saude_id)
                                     REFERENCES profissional_saude (id)
                                     ON DELETE RESTRICT
                                     ON UPDATE CASCADE
);

-- Tabela de receitas (ElementCollection)
CREATE TABLE atendimento_receita (
                                     atendimento_id BIGINT       NOT NULL,
                                     receita        VARCHAR(50)  NOT NULL,

                                     CONSTRAINT fk_receita_atendimento
                                         FOREIGN KEY (atendimento_id)
                                             REFERENCES atendimento (id)
                                             ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_atendimento_profissional ON atendimento (profissional_saude_id);