-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;
-- 1. PESSOAS (IDs: 1, 2, 3)
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (1, 'Yasmim Sampaio Benevides', 'FEMININO', '12345678901', '1995-04-12');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (2, 'Raphael Luiz Benevides', 'MASCULINO', '98765432100', '1991-09-18');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (3, 'Jaqueline Lemos Santos', 'FEMININO', '45678912304', '2001-08-05');

-- 2. VIAGENS (IDs: 4, 5)
INSERT INTO viagem (id, titulo, descricao, data_inicio, data_fim) VALUES (4, 'Mozao em Toulouse', 'Viagem para visitar meu mozao na frança', '2026-10-23', '2026-11-28');
INSERT INTO viagem (id, titulo, descricao, data_inicio, data_fim) VALUES (5, 'Férias na Bahia', 'Viagem para mostrara bahia pro mozao e mamae', '2027-01-15', '2027-01-30');

-- 3. ROTEIROS (IDs: 6, 7 - apontando para viagem_id = 4 e 5)
INSERT INTO roteiro (id, data, nome_atividade, descricao_atividade, valor, viagem_id) VALUES (6, '2026-11-02', 'Passeio em Bordeaux', 'Tour guiado no centro histórico', 120.00, 4);
INSERT INTO roteiro (id, data, nome_atividade, descricao_atividade, valor, viagem_id) VALUES (7, '2027-01-16', 'Pelourinho', 'Passeio em Salvador', 80.00, 5);

-- 4. VÍNCULOS DE PESSOAS NAS VIAGENS (viagem_pessoa)
INSERT INTO viagem_pessoa (viagem_id, pessoa_id) VALUES (4, 1);
INSERT INTO viagem_pessoa (viagem_id, pessoa_id) VALUES (4, 2);
INSERT INTO viagem_pessoa (viagem_id, pessoa_id) VALUES (5, 1);
INSERT INTO viagem_pessoa (viagem_id, pessoa_id) VALUES (5, 2);
INSERT INTO viagem_pessoa (viagem_id, pessoa_id) VALUES (5, 3);

-- 5. REINICIA A SEQUENCE A PARTIR DO ID 50 (evita conflito nos próximos POSTs)
ALTER SEQUENCE IF EXISTS entity_seq RESTART WITH 50;