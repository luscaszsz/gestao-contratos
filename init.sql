CREATE TABLE IF NOT EXISTS contrato (
    id NUMERIC PRIMARY KEY,
    numero VARCHAR(50) NOT NULL,
    contratante VARCHAR(255) NOT NULL,
    valor NUMERIC(15, 2) NOT NULL,
    data_inicio DATE NOT NULL
);

INSERT INTO contrato (id, numero, contratante, valor, data_inicio)
VALUES
    (1, 'CTR-001', 'Empresa Alfa', 15000.00, '2026-01-10'),
    (2, 'CTR-002', 'Empresa Beta', 27500.50, '2026-02-15'),
    (3, 'CTR-003', 'Empresa Gama', 8900.00, '2026-03-01'),
    (4, 'CTR-004', 'Empresa Delta', 42000.00, '2026-04-20'),
    (5, 'CTR-005', 'Empresa Épsilon', 12350.75, '2026-05-05');