
CREATE TABLE usuario (
    id NUMBER PRIMARY KEY,
    versao NUMBER DEFAULT 0,
    tipo_usuario VARCHAR2(20) NOT NULL,
    nome_completo VARCHAR2(100) NOT NULL,
    idade NUMBER NOT NULL,
    cpf VARCHAR2(11) UNIQUE NOT NULL,
    cep VARCHAR2(8) NOT NULL,
    numero NUMBER NOT NULL,
    complemento VARCHAR2(100),
    telefone VARCHAR2(15) NOT NULL,
    senha VARCHAR2(100) NOT NULL
);

CREATE TABLE apoiador (
    id NUMBER PRIMARY KEY,
    cargo VARCHAR2(50) NOT NULL,
    area_atuacao VARCHAR2(100),
    FOREIGN KEY (id) REFERENCES usuario(id)
);

CREATE TABLE paciente (
    id NUMBER PRIMARY KEY,
    cartao_sus VARCHAR2(20),
    telefone_contato VARCHAR2(15) NOT NULL,
    apoiador_id NUMBER,
    FOREIGN KEY (id) REFERENCES usuario(id),
    FOREIGN KEY (apoiador_id) REFERENCES apoiador(id)
);

CREATE TABLE teleconsulta (
    id NUMBER PRIMARY KEY,
    versao NUMBER DEFAULT 0,
    paciente_id NUMBER NOT NULL,
    medico VARCHAR2(100) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR2(20) DEFAULT 'AGENDADA',
    observacoes VARCHAR2(500),
    FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);

-- Sequences
CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_teleconsulta START WITH 1 INCREMENT BY 1;