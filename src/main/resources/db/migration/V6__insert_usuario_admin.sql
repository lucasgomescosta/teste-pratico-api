INSERT INTO usuario (login, senha, role)
VALUES (
  'admin',
  '$2a$10$roqEUjuWh9IovPNFONOQgO2/TqpYrb3Obo79YenGcZCxEk6Tjn2wy',
  'ADMIN'
) ON CONFLICT (login) DO NOTHING;