-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (nextval('pessoa_seq'), 'Yasmim Sampaio Benevides', 'FEMININO', '12345678901', '1995-04-12');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (nextval('pessoa_seq'), 'Bruno Henrique Oliveira', 'MASCULINO', '98765432100', '1988-11-23');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (nextval('pessoa_seq'), 'Carla Maria Santos', 'FEMININO', '45678912304', '2001-08-05');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (nextval('pessoa_seq'), 'Raphael Luiz Benevides', 'MASCULINO', '32165498702', '1991-09-18');
INSERT INTO pessoa (id, nome, sexo, cpf, data_nascimento) VALUES (nextval('pessoa_seq'), 'Elena Costa Lima', 'OUTRO', '65498732105', '1999-12-15');