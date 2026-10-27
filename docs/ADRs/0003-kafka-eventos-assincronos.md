# ADR 0003 — Kafka para comunicação assíncrona por eventos

- **Status:** Aceita
- **Data:** 2026-10-26

## Contexto

O fluxo de publicação e validação utiliza Apache Kafka. O serviço de publicação envia solicitações de validação, e o serviço de validação publica o resultado em um tópico distinto.

## Decisão

Usar Kafka como mecanismo de comunicação assíncrona entre publicação e validação, com os tópicos documentados:

- `contrato.validacao.solicitada`
- `contrato.validacao.resultado`

Grupos consumidores documentados:

- `valida-contratos`: consome solicitações de validação.
- `publica-contratos`: consome resultados de validação.

O Kafka desacopla temporalmente os serviços: o produtor publica uma mensagem e o consumidor a processa quando estiver disponível.

## Consequências

### Positivas
- Menor dependência de disponibilidade simultânea entre os serviços.
- Capacidade de absorver picos e processar mensagens de forma assíncrona.
- Possibilidade de reprocessamento conforme retenção e offsets configurados.

### Custos e riscos
- O processamento é assíncrono e o estado pode não ser atualizado imediatamente.
- Reentregas, duplicidade, rebalances e atrasos precisam ser tratados.
- O Kafka de nó único usado no ambiente de demonstração não oferece alta disponibilidade.

