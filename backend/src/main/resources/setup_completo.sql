-- ============================================================
-- SCRIPT COMPLETO - Sistema de Agenda / Saúde
-- PostgreSQL - Execute conectado ao banco agenda_db
-- ============================================================

-- Contatos
CREATE TABLE IF NOT EXISTS contatos (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    telefone   VARCHAR(20),
    email      VARCHAR(100),
    endereco   VARCHAR(200),
    criado_em  TIMESTAMP DEFAULT NOW()
);

-- Compromissos
CREATE TABLE IF NOT EXISTS compromissos (
    id         BIGSERIAL PRIMARY KEY,
    titulo     VARCHAR(200) NOT NULL,
    data       DATE         NOT NULL,
    hora       TIME,
    descricao  TEXT,
    contato_id BIGINT REFERENCES contatos(id) ON DELETE SET NULL,
    criado_em  TIMESTAMP DEFAULT NOW()
);

-- Profissionais de Saúde
CREATE TABLE IF NOT EXISTS profissionais_saude (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    telefone  VARCHAR(20),
    endereco  VARCHAR(250),
    categoria VARCHAR(20)  NOT NULL
        CONSTRAINT chk_categoria CHECK (categoria IN ('MEDICO', 'PSICOLOGO', 'FISIOTERAPEUTA'))
);

-- Atendimentos
CREATE TABLE IF NOT EXISTS atendimento (
    id                    BIGSERIAL PRIMARY KEY,
    data                  DATE         NOT NULL,
    horario               TIME         NOT NULL,
    titulo                VARCHAR(255) NOT NULL,
    link_videoconferencia VARCHAR(500),
    profissional_saude_id BIGINT NOT NULL REFERENCES profissionais_saude(id) ON DELETE RESTRICT
);

-- Receitas do Atendimento
CREATE TABLE IF NOT EXISTS atendimento_receita (
    atendimento_id BIGINT NOT NULL REFERENCES atendimento(id) ON DELETE CASCADE,
    receita        VARCHAR(30) NOT NULL
        CONSTRAINT chk_receita CHECK (receita IN ('REMEDIO', 'ATIVIDADE_FISICA', 'ATIVIDADE_MENTAL'))
);

-- Exames de Laboratório
CREATE TABLE IF NOT EXISTS exame_laboratorio (
    id             BIGSERIAL PRIMARY KEY,
    descricao      VARCHAR(255) NOT NULL,
    posologia      VARCHAR(255) NOT NULL,
    atendimento_id BIGINT NOT NULL REFERENCES atendimento(id) ON DELETE CASCADE
);

-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_compromisso_contato     ON compromissos(contato_id);
CREATE INDEX IF NOT EXISTS idx_compromisso_data        ON compromissos(data);
CREATE INDEX IF NOT EXISTS idx_profissional_nome       ON profissionais_saude(nome);
CREATE INDEX IF NOT EXISTS idx_profissional_categoria  ON profissionais_saude(categoria);
CREATE INDEX IF NOT EXISTS idx_atendimento_profissional ON atendimento(profissional_saude_id);
CREATE INDEX IF NOT EXISTS idx_atendimento_data        ON atendimento(data);
CREATE INDEX IF NOT EXISTS idx_exame_atendimento       ON exame_laboratorio(atendimento_id);

-- ============================================================
-- DADOS DE EXEMPLO
-- ============================================================

INSERT INTO contatos (nome, telefone, email, endereco) VALUES
    ('Ana Silva',    '(31) 99111-0001', 'ana@email.com',   'Rua A, 100 - BH/MG'),
    ('Bruno Costa',  '(31) 99111-0002', 'bruno@email.com', 'Rua B, 200 - BH/MG'),
    ('Carla Mendes', '(31) 99111-0003', 'carla@email.com', 'Rua C, 300 - BH/MG');

INSERT INTO compromissos (titulo, data, hora, descricao, contato_id) VALUES
    ('Reunião de projeto', '2026-06-20', '10:00', 'Reunião inicial do projeto', 1),
    ('Consulta médica',    '2026-06-22', '14:30', 'Retorno consulta',           2);

INSERT INTO profissionais_saude (nome, telefone, endereco, categoria) VALUES
    ('Dr. Carlos Mendes',    '(31) 98000-0001', 'Av. Afonso Pena, 500 - BH/MG',   'MEDICO'),
    ('Psi. Ana Paula Costa', '(31) 98000-0002', 'Rua da Bahia, 1200 - BH/MG',     'PSICOLOGO'),
    ('Fis. Roberto Alves',   '(31) 98000-0003', 'Rua Espírito Santo, 300 - BH/MG','FISIOTERAPEUTA');

INSERT INTO atendimento (data, horario, titulo, link_videoconferencia, profissional_saude_id) VALUES
    ('2026-06-25', '09:00', 'Consulta de rotina', NULL,                         1),
    ('2026-06-26', '15:00', 'Sessão de terapia',  'https://meet.example.com/1', 2);

INSERT INTO atendimento_receita (atendimento_id, receita) VALUES
    (1, 'REMEDIO'),
    (1, 'ATIVIDADE_FISICA'),
    (2, 'ATIVIDADE_MENTAL');

INSERT INTO exame_laboratorio (descricao, posologia, atendimento_id) VALUES
    ('Hemograma completo', 'Jejum de 8 horas',  1),
    ('Glicemia em jejum',  'Jejum de 12 horas', 1);
