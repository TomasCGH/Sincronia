CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS pais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS departamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pais_id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT fk_departamento_pais FOREIGN KEY (pais_id) REFERENCES pais(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ciudad (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    departamento_id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ciudad_departamento FOREIGN KEY (departamento_id) REFERENCES departamento(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS administrador (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    correo VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS conjunto_residencial (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    ciudad_id UUID NOT NULL,
    administrador_id UUID NOT NULL,
    telefono VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT fk_conjunto_ciudad FOREIGN KEY (ciudad_id) REFERENCES ciudad(id) ON DELETE CASCADE,
    CONSTRAINT fk_conjunto_administrador FOREIGN KEY (administrador_id) REFERENCES administrador(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS vivienda (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(20) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    conjunto_id UUID NOT NULL,
    CONSTRAINT fk_vivienda_conjunto FOREIGN KEY (conjunto_id) REFERENCES conjunto_residencial(id) ON DELETE CASCADE
);
