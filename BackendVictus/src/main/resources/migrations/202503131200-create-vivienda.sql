CREATE TABLE IF NOT EXISTS vivienda (
    id UUID PRIMARY KEY,
    numero VARCHAR(10) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Disponible',
    conjunto_id UUID NOT NULL REFERENCES conjunto_residencial(id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (conjunto_id, numero)
);

CREATE INDEX IF NOT EXISTS idx_vivienda_conjunto ON vivienda (conjunto_id);
