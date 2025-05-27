USE Chefit_DB;

CREATE TABLE usuario
 
(
 
    id            INT PRIMARY KEY,
 
    nome          VARCHAR(100)  NOT NULL,
 
    idade         INTEGER       NOT NULL,
 
    peso          DECIMAL(5, 2) NOT NULL,
 
    altura        DECIMAL(3, 2) NOT NULL,
 
    sexo          CHAR(1)       NOT NULL,
 
    objetivo      VARCHAR(20)   NOT NULL,
 
    tipo_dieta    VARCHAR(20)   NOT NULL,
 
    email         VARCHAR(100)  UNIQUE NOT NULL,
 
    senha         VARCHAR(255)  NOT NULL,
 
    permissao     VARCHAR(20)   NOT NULL DEFAULT 'CONSUMIDOR',
 
	);
 
 
	select*from usuario
 
-- Rest of the tables stay the same
 
CREATE TABLE receitas
 
(
 
    id                  INT PRIMARY KEY,
 
    nome                VARCHAR(100) NOT NULL,
 
    descricao           TEXT,
 
    tempo_preparo       INTEGER      NOT NULL,
 
    porcoes             INTEGER      NOT NULL,
 
    modo_preparo        TEXT         NOT NULL,
 
    categoria           VARCHAR(20)  NOT NULL,
 
    calorias_porcao     INTEGER      NOT NULL,
 
    proteinas_porcao    DECIMAL(5, 2),
 
    carboidratos_porcao DECIMAL(5, 2),
 
    gorduras_porcao     DECIMAL(5, 2),      
 
	);
 
	SELECT*FROM receitas
 
CREATE TABLE ingredientes
 
(
 
    id             INT PRIMARY KEY,
 
    nome           VARCHAR(100) NOT NULL,
 
    unidade_medida VARCHAR(20)  NOT NULL
 
);
 
CREATE TABLE receitas_ingredientes
 
(
 
    receita_id     INTEGER REFERENCES receitas (id),
 
    ingrediente_id INTEGER REFERENCES ingredientes (id),
 
    quantidade     DECIMAL(6, 2) NOT NULL,
 
    PRIMARY KEY (receita_id, ingrediente_id)
 
);
 
CREATE TABLE usuario_receitas_favoritas
 
(
 
    usuario_id INTEGER REFERENCES usuario (id),
 
    receita_id INTEGER REFERENCES receitas (id),
 
    PRIMARY KEY (usuario_id, receita_id)
 
);
 
 
-- Insert initial users with all required fields
 
INSERT INTO usuario (nome,id ,idade, peso, altura, sexo, objetivo, tipo_dieta, email, senha, permissao)
 
VALUES ('João Silva',21011 ,28, 75.5, 1.75, 'M', 'GANHO_MASSA', 'PROTEICA', 'joao.silva@email.com',
 
        '$2a$10$yOv9U3rsEeZL6GGrHZWcqeJBtw4JvFW3kwfP/MyhHYqFi7DFDLKtG', 'CONSUMIDOR' ),
 
       ('Maria Santos',31022 ,32, 65.0, 1.65, 'F', 'PERDA_PESO', 'LOW_CARB', 'maria.santos@email.com',
 
        '$2a$10$yOv9U3rsEeZL6GGrHZWcqeJBtw4JvFW3kwfP/MyhHYqFi7DFDLKtG', 'CONSUMIDOR'),
 
       ('Pedro Oliveira',41033,45, 88.2, 1.80, 'M', 'SAUDE', 'MEDITERRANEA', 'pedro.oliveira@email.com',
 
        '$2a$10$yOv9U3rsEeZL6GGrHZWcqeJBtw4JvFW3kwfP/MyhHYqFi7DFDLKtG', 'NUTRICIONISTA'),
 
       ('Ana Costa',51044 ,25, 58.5, 1.60, 'F', 'PERDA_PESO', 'VEGETARIANA', 'ana.costa@email.com',
 
        '$2a$10$yOv9U3rsEeZL6GGrHZWcqeJBtw4JvFW3kwfP/MyhHYqFi7DFDLKtG', 'CONSUMIDOR'),
 
       ('Lucas Mendes',61055 , 30, 82.0, 1.78, 'M', 'GANHO_MASSA', 'PROTEICA', 'admin@chefit.com',
 
        '$2a$10$yOv9U3rsEeZL6GGrHZWcqeJBtw4JvFW3kwfP/MyhHYqFi7DFDLKtG', 'ADMIN');
 
-- Insert ingredients
 
INSERT INTO ingredientes (nome,id ,unidade_medida)
 
VALUES ('Peito de Frango',1,'gramas'),
 
       ('Arroz Integral',2,'gramas'),
 
       ('Azeite de Oliva',3,'ml'),
 
       ('Brócolis',4,'gramas'),
 
       ('Batata Doce',5,'gramas'),
 
       ('Ovo',6,'unidade'),
 
       ('Whey Protein',7,'gramas'),
 
       ('Banana',8,'unidade'),
 
       ('Aveia',9,'gramas'),
 
       ('Leite Desnatado',10,'ml'),
 
       ('Quinoa',11,'gramas'),
 
       ('Salmão',12,'gramas'),
 
       ('Espinafre',13, 'gramas'),
 
       ('Lentilha',14,'gramas'),
 
       ('Tomate',15,'gramas');
 
-- Inserir Receitas
 
INSERT INTO receitas (id, nome, descricao, tempo_preparo, porcoes, modo_preparo, categoria, calorias_porcao,

                      proteinas_porcao, carboidratos_porcao, gorduras_porcao)

VALUES 

  (10, 'Bowl de Frango Fit', 'Bowl proteico pós-treino', 30, 1, 'Cozinhe o arroz...', 'ALMOCO', 450, 35.0, 45.0, 15.0),

  (22, 'Shake Proteico', 'Shake pré-treino energético', 5, 1, 'Bata no liquidificador...', 'LANCHE', 300, 25.0, 30.0, 8.0),

  (36, 'Salmão com Quinoa', 'Prato rico em ômega 3', 40, 2, 'Cozinhe a quinoa...', 'JANTAR', 380, 28.0, 32.0, 18.0),

  (49, 'Omelete de Legumes', 'Café da manhã proteico', 15, 1, 'Bata os ovos...', 'CAFE_MANHA', 250, 18.0, 8.0, 16.0),

  (54, 'Salada de Lentilha', 'Salada vegetariana proteica', 25, 2, 'Cozinhe a lentilha...', 'ALMOCO', 280, 15.0, 35.0, 10.0);
 
 
	--ROP TABLE receitas
 
-- Inserir Relacionamentos Receitas-Ingredientes
 
INSERT INTO receitas_ingredientes (receita_id, ingrediente_id, quantidade)
 
VALUES (10, 1, 200),  -- Frango para Bowl
 
       (10, 2, 100),  -- Arroz para Bowl
 
       (10, 3, 15),   -- Azeite para Bowl
 
       (10, 4, 100),  -- Brócolis para Bowl
 
       (022, 7, 30),   -- Whey para Shake
 
       (022, 8, 1),    -- Banana para Shake
 
       (022, 9, 30),   -- Aveia para Shake
 
       (022, 10, 200), -- Leite para Shake
 
       (036, 12, 180), -- Salmão
 
       (036, 11, 100), -- Quinoa
 
       (036, 13, 100), -- Espinafre
 
       (049, 6, 3),    -- Ovos para Omelete
 
       (049, 15, 50),  -- Tomate para Omelete
 
       (054, 14, 150), -- Lentilha
 
       (054, 15, 100); -- Tomate para Salada
 

 
-- Inserir Receitas Favoritas dos Usuários
 
INSERT INTO usuario_receitas_favoritas (usuario_id, receita_id)

VALUES 

  (21011, 10),

  (21011, 22),

  (31022, 49),

  (31022, 54),

  (41033, 36),

  (51044, 54),

  (61055, 10),

  (61055, 22);
 

 -- Mostra o Nome do Usuario e sua Receita favorita
 
SELECT 

    u.nome AS nome_usuario,

    r.nome AS nome_receita

FROM 

    usuario_receitas_favoritas urf

JOIN 

    usuario u ON urf.usuario_id = u.id

JOIN 

    receitas r ON urf.receita_id = r.id;
 
 
	-- mostra o id dos usuarios e suas receita favorita
 
	SELECT 

    urf.usuario_id,

    r.nome AS nome_receita

FROM 

    usuario_receitas_favoritas urf

JOIN 

    receitas r ON urf.receita_id = r.id;
 
 
	--Exibe o ID do usuário, o nome dele, e o nome da receita favorita
 
	SELECT 

    u.id AS usuario_id,

    u.nome AS nome_usuario,

    r.nome AS nome_receita

FROM 

    usuario_receitas_favoritas urf

JOIN 

    usuario u ON urf.usuario_id = u.id

JOIN 

    receitas r ON urf.receita_id = r.id;

 