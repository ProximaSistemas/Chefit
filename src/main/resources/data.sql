DELETE FROM receitas_ingredientes;
DELETE FROM receitas;
DELETE FROM ingredientes;
DELETE FROM usuarios_receitas_favoritas;
DELETE FROM usuarios;

-- Inserir Usuários
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES ('João Silva', 28, 75.5, 1.75, 'M', 'GANHO_MASSA', 'PROTEICA', TIMESTAMP '2024-11-22 10:00:00');
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES ('Maria Santos', 32, 65.0, 1.65, 'F', 'PERDA_PESO', 'LOW_CARB', TIMESTAMP '2024-11-22 10:00:00');
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES ('Pedro Oliveira', 45, 88.2, 1.80, 'M', 'SAUDE', 'MEDITERRANEA', TIMESTAMP '2024-11-22 10:00:00');
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES ('Ana Costa', 25, 58.5, 1.60, 'F', 'PERDA_PESO', 'VEGETARIANA', TIMESTAMP '2024-11-22 10:00:00');
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES ('Lucas Mendes', 30, 82.0, 1.78, 'M', 'GANHO_MASSA', 'PROTEICA', TIMESTAMP '2024-11-22 10:00:00');

-- Inserir Ingredientes
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Peito de Frango', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Arroz Integral', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Azeite de Oliva', 'ml');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Brócolis', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Batata Doce', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Ovo', 'unidade');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Whey Protein', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Banana', 'unidade');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Aveia', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Leite Desnatado', 'ml');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Quinoa', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Salmão', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Espinafre', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Lentilha', 'gramas');
INSERT INTO ingredientes (nome, unidade_medida) VALUES ('Tomate', 'gramas');

-- Inserir Receitas
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao, data_cadastro) VALUES ('Bowl de Frango Fit', 'Bowl proteico pós-treino', 30, 1, 'Cozinhe o arroz integral. Grelhe o peito de frango temperado. Cozinhe os brócolis no vapor. Monte o bowl com arroz na base, frango e brócolis. Finalize com azeite.', 'ALMOCO', 450, 35.0, 45.0, 15.0, CURRENT_TIMESTAMP);
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao, data_cadastro) VALUES ('Shake Proteico', 'Shake pré-treino energético', 5, 1, 'Bata no liquidificador: whey protein, banana, aveia e leite desnatado.', 'LANCHE', 300, 25.0, 30.0, 8.0, CURRENT_TIMESTAMP);
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao, data_cadastro) VALUES ('Salmão com Quinoa', 'Prato rico em ômega 3', 40, 2, 'Cozinhe a quinoa. Grelhe o salmão temperado. Refogue o espinafre. Sirva o salmão sobre a quinoa com espinafre ao lado.', 'JANTAR', 380, 28.0, 32.0, 18.0, CURRENT_TIMESTAMP);
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao, data_cadastro) VALUES ('Omelete de Legumes', 'Café da manhã proteico', 15, 1, 'Bata os ovos, adicione os legumes picados. Cozinhe em fogo médio até dourar.', 'CAFE_MANHA', 250, 18.0, 8.0, 16.0, CURRENT_TIMESTAMP);
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao, data_cadastro) VALUES ('Salada de Lentilha', 'Salada vegetariana proteica', 25, 2, 'Cozinhe a lentilha. Misture com tomates picados e tempere com azeite.', 'ALMOCO', 280, 15.0, 35.0, 10.0, CURRENT_TIMESTAMP);

-- Inserir Relacionamentos Receitas-Ingredientes
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (1, 1, 200);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (1, 2, 100);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (1, 3, 15);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (1, 4, 100);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (2, 7, 30);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (2, 8, 1);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (2, 9, 30);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (2, 10, 200);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (3, 12, 180);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (3, 11, 100);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (3, 13, 100);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (4, 6, 3);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (4, 15, 50);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (5, 14, 150);
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES (5, 15, 100);

-- Inserir Receitas Favoritas dos Usuários
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (1, 1);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (1, 2);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (2, 4);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (2, 5);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (3, 3);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (4, 5);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (5, 1);
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES (5, 2);
