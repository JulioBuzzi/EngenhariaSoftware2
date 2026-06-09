CREATE TABLE atendimento (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    descricao_sintomas TEXT
);

CREATE TABLE exame_laboratorio (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    posologia VARCHAR(500) NOT NULL,
    atendimento_id BIGINT NOT NULL,
    CONSTRAINT fk_exame_atendimento FOREIGN KEY (atendimento_id) REFERENCES atendimento(id) ON DELETE CASCADE
);