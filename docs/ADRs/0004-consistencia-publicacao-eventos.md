# ADR 0004 — Consistência entre persistência e publicação de eventos

- **Status:** Proposta
- **Data:** 2026-10-26

## Contexto

O serviço `publica-contrato` persiste dados em PostgreSQL e publica um evento Kafka. Se a gravação no banco for confirmada, mas a publicação falhar (ou vice-versa), pode surgir divergência entre o estado persistido e o fluxo de eventos. Não há evidência, na arquitetura documentada, de que esse problema já esteja resolvido.

## Decisão proposta

Avaliar a implementação do padrão **Transactional Outbox** no serviço de publicação:

1. Gravar os dados do contrato e um registro de evento pendente na mesma transação PostgreSQL.
2. Um publicador separado lê os registros pendentes e publica os eventos no Kafka.
3. Após confirmação de publicação, marcar o registro como enviado, com estratégia segura para falhas e reenvios.

O consumidor deve ser idempotente, pois uma falha entre a publicação e a marcação de envio pode provocar reentrega.

## Consequências

### Positivas
- Evita depender de uma transação distribuída entre PostgreSQL e Kafka.
- Reduz o risco de persistência sem evento correspondente.
- Permite retomar a publicação após falhas.

### Custos e riscos
- Acrescenta tabela, processo de publicação, limpeza e monitoramento.
- Não elimina duplicidade por si só; consumidores devem suportá-la.
- Exige política para registros antigos, retries e mensagens permanentemente inválidas.

## Critérios de implementação

- Identificador único do evento e identificador de negócio do contrato.
- Estado/timestamp de publicação e tentativas, sem guardar segredos no payload.
- Idempotência no consumidor, idealmente persistida no banco quando o efeito de negócio precisar sobreviver a reinícios.
- Testes de falha entre commit do banco, envio Kafka e confirmação de envio.

## Alternativa

Uma publicação direta após o commit é mais simples, mas mantém a janela de inconsistência. A decisão final deve considerar criticidade, volume e garantias operacionais exigidas.
