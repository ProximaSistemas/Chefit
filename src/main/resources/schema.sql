DROP TABLE IF EXISTS usuarios_receitas_favoritas;
DROP TABLE IF EXISTS receitas_ingredientes;
DROP TABLE IF EXISTS receitas;
DROP TABLE IF EXISTS ingredientes;
DROP TABLE IF EXISTS usuarios;


CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    idade INTEGER NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    altura DECIMAL(3,2) NOT NULL,
    sexo CHAR(1) NOT NULL,
    objetivo VARCHAR(20) NOT NULL,
    tipo_dieta VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE receitas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao CLOB,
    tempo_preparo INTEGER NOT NULL,
    porcoes INTEGER NOT NULL,
    modo_preparo CLOB NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    calorias_porcao INTEGER NOT NULL,
    proteinas_porcao DECIMAL(5,2),
    carboidratos_porcao DECIMAL(5,2),
    gorduras_porcao DECIMAL(5,2),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE ingredientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL
);

CREATE TABLE receitas_ingredientes (
    receita_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    quantidade DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (receita_id, ingrediente_id),
    FOREIGN KEY (receita_id) REFERENCES receitas(id),
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

CREATE TABLE usuarios_receitas_favoritas (
    usuario_id BIGINT NOT NULL,
    receita_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, receita_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (receita_id) REFERENCES receitas(id)
);
