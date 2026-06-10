-- Tabela de profissionais de saúde
CREATE TABLE IF NOT EXISTS profissionais_saude (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(150)   NOT NULL,
    telefone   VARCHAR(20),
    endereco   VARCHAR(250),
    categoria  VARCHAR(20)    NOT NULL
        CONSTRAINT chk_categoria CHECK (categoria IN ('MEDICO', 'PSICOLOGO', 'FISIOTERAPEUTA'))
);

-- Índice para buscas por nome
CREATE INDEX IF NOT EXISTS idx_profissional_nome ON profissionais_saude (nome);

-- Índice para filtros por categoria
CREATE INDEX IF NOT EXISTS idx_profissional_categoria ON profissionais_saude (categoria);
