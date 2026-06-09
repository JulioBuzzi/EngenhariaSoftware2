-- Script SQL para a entidade ProfissionalSaude
-- PostgreSQL

-- Tipo ENUM para categoria do profissional de saúde
CREATE TYPE categoria_enum AS ENUM ('MEDICO', 'PSICOLOGO', 'FISIOTERAPEUTA');

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

-- Dados de exemplo
INSERT INTO profissionais_saude (nome, telefone, endereco, categoria) VALUES
    ('Dr. Carlos Mendes',       '(31) 99111-2233', 'Av. Afonso Pena, 500 - Centro, BH/MG',     'MEDICO'),
    ('Dra. Ana Paula Costa',    '(31) 98222-3344', 'Rua da Bahia, 1200 - Lourdes, BH/MG',      'PSICOLOGO'),
    ('Fis. Roberto Alves',      '(31) 97333-4455', 'Rua Espírito Santo, 300 - Centro, BH/MG',  'FISIOTERAPEUTA'),
    ('Dra. Fernanda Lima',      '(31) 96444-5566', 'Av. do Contorno, 800 - Savassi, BH/MG',    'MEDICO'),
    ('Psi. Marcos Oliveira',    '(31) 95555-6677', 'Rua Sergipe, 450 - Funcionários, BH/MG',   'PSICOLOGO');
