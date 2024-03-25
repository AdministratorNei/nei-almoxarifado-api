ALTER TABLE Itens
DROP COLUMN quantidade_emprestados;

ALTER TABLE Itens
RENAME COLUMN quantidade_disponiveis TO quantidade;

ALTER TABLE Itens
ALTER COLUMN tombamento DROP NOT NULL;

ALTER TABLE Itens
RENAME COLUMN tombamento TO codigo_sipac;

ALTER TABLE Itens
DROP COLUMN ativo;