-- Criação da tabela pagamentos baseada na entidade Pagamento
-- Banco: PostgreSQL

CREATE TABLE IF NOT EXISTS pagamentos (
    id BIGSERIAL PRIMARY KEY,
    valor NUMERIC(19,2) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    numero VARCHAR(19) NOT NULL,
    expiracao VARCHAR(7) NOT NULL,
    codigo VARCHAR(3) NOT NULL,
    status VARCHAR(12) NOT NULL,
    pedido_id BIGINT NOT NULL,
    forma_de_pagamento_id BIGINT NOT NULL,
    CONSTRAINT chk_pagamentos_status CHECK (status IN ('CRIADO', 'CONFIRMADO', 'CANCELADO'))
);
