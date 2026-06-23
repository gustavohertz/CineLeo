-- Salas (unique constraint no nome)
INSERT INTO salas (nome, capacidade, tipo) VALUES
('Sala 1', 120, 'STANDARD'),
('Sala 2', 80, 'VIP'),
('Sala 3', 200, 'IMAX')
ON CONFLICT (nome) DO NOTHING;

-- Filmes (sem unique constraint, usar verificação)
INSERT INTO filmes (nome, classificacao_indicativa, status, logradouro, numero, bairro, cidade, uf, cep)
SELECT 'O Senhor dos Anéis', 'QUATORZE_ANOS', 'ATIVO', 'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', '01310-100'
WHERE NOT EXISTS (SELECT 1 FROM filmes WHERE nome = 'O Senhor dos Anéis');

INSERT INTO filmes (nome, classificacao_indicativa, status, logradouro, numero, bairro, cidade, uf, cep)
SELECT 'Matrix', 'DEZESSEIS_ANOS', 'ATIVO', 'Rua Augusta', '500', 'Consolação', 'São Paulo', 'SP', '01304-000'
WHERE NOT EXISTS (SELECT 1 FROM filmes WHERE nome = 'Matrix');

INSERT INTO filmes (nome, classificacao_indicativa, status, logradouro, numero, bairro, cidade, uf, cep)
SELECT 'Toy Story', 'LIVRE', 'ATIVO', 'Rua Oscar Freire', '900', 'Jardim Paulista', 'São Paulo', 'SP', '01426-000'
WHERE NOT EXISTS (SELECT 1 FROM filmes WHERE nome = 'Toy Story');

-- Sessões usando subconsultas escalares (compatível com Spring SQL init)
INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis'),
    (SELECT id FROM salas WHERE nome = 'Sala 1'),
    '2026-07-01 14:00:00', '2026-07-01 17:00:00', 35.00, 'AGENDADA', 120
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 1')
    AND data_hora_inicio = '2026-07-01 14:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis'),
    (SELECT id FROM salas WHERE nome = 'Sala 1'),
    '2026-07-01 19:00:00', '2026-07-01 22:00:00', 35.00, 'AGENDADA', 120
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 1')
    AND data_hora_inicio = '2026-07-01 19:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis'),
    (SELECT id FROM salas WHERE nome = 'Sala 3'),
    '2026-07-01 16:00:00', '2026-07-01 19:00:00', 45.00, 'AGENDADA', 200
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'O Senhor dos Anéis')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 3')
    AND data_hora_inicio = '2026-07-01 16:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'Matrix'),
    (SELECT id FROM salas WHERE nome = 'Sala 2'),
    '2026-07-01 15:00:00', '2026-07-01 17:30:00', 28.00, 'AGENDADA', 80
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'Matrix')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 2')
    AND data_hora_inicio = '2026-07-01 15:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'Matrix'),
    (SELECT id FROM salas WHERE nome = 'Sala 2'),
    '2026-07-01 20:00:00', '2026-07-01 22:30:00', 32.00, 'AGENDADA', 80
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'Matrix')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 2')
    AND data_hora_inicio = '2026-07-01 20:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'Toy Story'),
    (SELECT id FROM salas WHERE nome = 'Sala 1'),
    '2026-07-01 10:00:00', '2026-07-01 11:30:00', 20.00, 'AGENDADA', 120
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'Toy Story')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 1')
    AND data_hora_inicio = '2026-07-01 10:00:00'
);

INSERT INTO sessoes (filme_id, sala_id, data_hora_inicio, data_hora_fim, preco, status, assentos_disponiveis)
SELECT
    (SELECT id FROM filmes WHERE nome = 'Toy Story'),
    (SELECT id FROM salas WHERE nome = 'Sala 3'),
    '2026-07-01 13:00:00', '2026-07-01 14:30:00', 25.00, 'AGENDADA', 200
WHERE NOT EXISTS (
    SELECT 1 FROM sessoes
    WHERE filme_id = (SELECT id FROM filmes WHERE nome = 'Toy Story')
    AND sala_id = (SELECT id FROM salas WHERE nome = 'Sala 3')
    AND data_hora_inicio = '2026-07-01 13:00:00'
);