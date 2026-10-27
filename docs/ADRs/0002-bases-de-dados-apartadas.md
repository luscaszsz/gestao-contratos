# ADR 0002 — Bases de dados apartadas por serviço

- **Status:** Aceita
- **Data:** 2026-10-26

## Contexto

A aplicação possui um PostgreSQL para os dados de publicação e outro PostgreSQL para o serviço auxiliar de consulta. O serviço de consulta também utiliza Redis. Os bancos são separados para manter o isolamento entre as responsabilidades.

## Decisão

Manter propriedade dos dados por serviço:

- `publica-contrato` é responsável pelos dados de publicação e pelo estado do processamento sob sua responsabilidade.
- `consulta-contratos` é responsável pelos dados usados para responder consultas.
- `valida-contrato` executa as regras de validação e comunica seus resultados por Kafka; conforme a implementação documentada, não precisa acessar o PostgreSQL de publicação para validar.

A integração entre os bancos deve ocorrer por contratos de API ou eventos explícitos, nunca por leitura ou escrita direta no banco alheio.

## Consequências

### Positivas
- Isolamento de falhas, permissões e evolução de esquema.
- Possibilidade de ajustar o modelo de consulta às necessidades de leitura.
- Redução do acoplamento entre os serviços.

### Custos e riscos
- Não há transação ACID única abrangendo os dois bancos.
- A replicação de dados entre publicação e consulta será eventualmente consistente.
- Migrações, backup, observabilidade e credenciais precisam ser gerenciados por banco/serviço.

