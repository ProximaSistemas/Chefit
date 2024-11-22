CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    idade INTEGER NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    altura DECIMAL(3,2) NOT NULL,
    sexo CHAR(1) NOT NULL,
    objetivo VARCHAR(20) NOT NULL,
    tipo_dieta VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE receitas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    tempo_preparo INTEGER NOT NULL,
    porcoes INTEGER NOT NULL,
    modo_preparo TEXT NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    calorias_porcao INTEGER NOT NULL,
    proteinas_porcao DECIMAL(5,2),
    carboidratos_porcao DECIMAL(5,2),
    gorduras_porcao DECIMAL(5,2),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL
);

CREATE TABLE receitas_ingredientes (
    receita_id INTEGER REFERENCES receitas(id),
    ingrediente_id INTEGER REFERENCES ingredientes(id),
    quantidade DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (receita_id, ingrediente_id)
);

CREATE TABLE usuarios_receitas_favoritas (
    usuario_id INTEGER REFERENCES usuarios(id),
    receita_id INTEGER REFERENCES receitas(id),
    PRIMARY KEY (usuario_id, receita_id)
);

-- Inserir Usuários
INSERT INTO usuarios (nome, idade, peso, altura, sexo, objetivo, tipo_dieta, data_cadastro) VALUES
('João Silva', 28, 75.5, 1.75, 'M', 'GANHO_MASSA', 'PROTEICA', '2024-11-22 10:00:00'),
('Maria Santos', 32, 65.0, 1.65, 'F', 'PERDA_PESO', 'LOW_CARB', '2024-11-22 10:00:00'),
('Pedro Oliveira', 45, 88.2, 1.80, 'M', 'SAUDE', 'MEDITERRANEA', '2024-11-22 10:00:00'),
('Ana Costa', 25, 58.5, 1.60, 'F', 'PERDA_PESO', 'VEGETARIANA', '2024-11-22 10:00:00'),
('Lucas Mendes', 30, 82.0, 1.78, 'M', 'GANHO_MASSA', 'PROTEICA', '2024-11-22 10:00:00');

-- Inserir Ingredientes
INSERT INTO ingredientes (nome, unidade_medida) VALUES
('Peito de Frango', 'gramas'),
('Arroz Integral', 'gramas'),
('Azeite de Oliva', 'ml'),
('Brócolis', 'gramas'),
('Batata Doce', 'gramas'),
('Ovo', 'unidade'),
('Whey Protein', 'gramas'),
('Banana', 'unidade'),
('Aveia', 'gramas'),
('Leite Desnatado', 'ml'),
('Quinoa', 'gramas'),
('Salmão', 'gramas'),
('Espinafre', 'gramas'),
('Lentilha', 'gramas'),
('Tomate', 'gramas');

-- Inserir Receitas
INSERT INTO receitas (nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao, proteinas_porcao, carboidratos_porcao, gorduras_porcao) VALUES
('Bowl de Frango Fit', 'Bowl proteico pós-treino', 30, 1, 'Cozinhe o arroz integral. Grelhe o peito de frango temperado. Cozinhe os brócolis no vapor. Monte o bowl com arroz na base, frango e brócolis. Finalize com azeite.', 'ALMOCO', 450, 35.0, 45.0, 15.0),
('Shake Proteico', 'Shake pré-treino energético', 5, 1, 'Bata no liquidificador: whey protein, banana, aveia e leite desnatado.', 'LANCHE', 300, 25.0, 30.0, 8.0),
('Salmão com Quinoa', 'Prato rico em ômega 3', 40, 2, 'Cozinhe a quinoa. Grelhe o salmão temperado. Refogue o espinafre. Sirva o salmão sobre a quinoa com espinafre ao lado.', 'JANTAR', 380, 28.0, 32.0, 18.0),
('Omelete de Legumes', 'Café da manhã proteico', 15, 1, 'Bata os ovos, adicione os legumes picados. Cozinhe em fogo médio até dourar.', 'CAFE_MANHA', 250, 18.0, 8.0, 16.0),
('Salada de Lentilha', 'Salada vegetariana proteica', 25, 2, 'Cozinhe a lentilha. Misture com tomates picados e tempere com azeite.', 'ALMOCO', 280, 15.0, 35.0, 10.0);

-- Inserir Relacionamentos Receitas-Ingredientes
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade) VALUES
(1, 1, 200), -- Frango para Bowl
(1, 2, 100), -- Arroz para Bowl
(1, 3, 15),  -- Azeite para Bowl
(1, 4, 100), -- Brócolis para Bowl
(2, 7, 30),  -- Whey para Shake
(2, 8, 1),   -- Banana para Shake
(2, 9, 30),  -- Aveia para Shake
(2, 10, 200), -- Leite para Shake
(3, 12, 180), -- Salmão
(3, 11, 100), -- Quinoa
(3, 13, 100), -- Espinafre
(4, 6, 3),    -- Ovos para Omelete
(4, 15, 50),  -- Tomate para Omelete
(5, 14, 150), -- Lentilha
(5, 15, 100); -- Tomate para Salada

-- Inserir Receitas Favoritas dos Usuários
INSERT INTO usuarios_receitas_favoritas (usuario_id, receita_id) VALUES
(1, 1), -- João - Bowl de Frango
(1, 2), -- João - Shake Proteico
(2, 4), -- Maria - Omelete
(2, 5), -- Maria - Salada de Lentilha
(3, 3), -- Pedro - Salmão
(4, 5), -- Ana - Salada de Lentilha
(5, 1), -- Lucas - Bowl de Frango
(5, 2); -- Lucas - Shake Proteico