INSERT INTO cliente (nme_cliente, dta_nascimento, nro_cpf, dta_criacao)
VALUES
    ('Lucas Gomes', DATE '1995-01-10', '12345678900', CURRENT_TIMESTAMP),
    ('Maria Silva', DATE '1990-05-20', '98765432100', CURRENT_TIMESTAMP);

INSERT INTO endereco (nme_logradouro, nme_bairro, nro_cep, nme_cidade, nme_estado)
VALUES
    ('Rua A', 'Centro', '69300000', 'Boa Vista', 'RR'),
    ('Rua B', 'Cauamé', '69301000', 'Boa Vista', 'RR');