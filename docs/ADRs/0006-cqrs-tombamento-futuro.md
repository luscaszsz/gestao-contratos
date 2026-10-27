# ADR 0006 — CQRS futuro: projeção de publicações para consulta

- **Status:** Proposta
- **Data:** 2026-10-26

## Contexto

Hoje, `publica-contrato` persiste dados em seu próprio PostgreSQL, enquanto `consulta-contratos` possui outra base PostgreSQL e Redis. A consulta não está conectada ao fluxo de eventos de publicação. Pretende-se, futuramente, que os dados publicados sejam disponibilizados no modelo de leitura por meio de uma integração assíncrona — chamada neste projeto de “tombamento”.

## Decisão proposta

Evoluir para uma separação de escrita e leitura inspirada em **CQRS**:

- **Modelo de escrita**: `publica-contrato` continua sendo a autoridade para registrar a publicação e seu estado transacional.
- **Eventos de integração**: após a persistência, publicar eventos de negócio versionados, como `ContratoPublicado` e, quando aplicável, `ContratoAtualizado` ou `ContratoCancelado`.
- **Modelo de leitura**: um consumidor atualiza a base PostgreSQL de `consulta-contratos` com uma projeção própria, preparada para as consultas necessárias.
- **Cache**: Redis continua sendo uma camada de cache sobre o modelo de leitura; não é o mecanismo de tombamento nem a fonte de verdade.

A entrega dos dados à consulta deve ser assíncrona e eventualmente consistente. O evento representa um fato ocorrido, não uma instrução para o serviço de consulta consultar diretamente o banco de publicação.

## Fluxo futuro pretendido

```text
Cliente
  |
  v
publica-contrato
  |-- grava contrato + evento pendente (Outbox, se adotado)
  |
  v
Kafka: eventos de contrato
  |
  v
consumidor/projetor de consulta
  |
  v
PostgreSQL de consulta (modelo de leitura)
  |
  v
Redis (cache-aside)
  |
  v
consulta-contratos -> Cliente
```

## Regras de consistência

- O PostgreSQL de publicação é a fonte de verdade para a escrita; o PostgreSQL de consulta é uma projeção derivada.
- Não se deve prometer leitura imediata após a publicação. A API precisa definir como comunicar eventual atraso de propagação.
- Não replicar dados pessoais ou campos desnecessários. Aplicar minimização, controle de acesso e retenção.

## Consequências

### Positivas
- Consultas podem ter esquema e índices independentes do modelo de escrita.
- Reduz a dependência síncrona entre publicação e consulta.
- Permite escalar separadamente processamento de eventos e leitura.

### Custos e riscos
- A consulta pode ficar temporariamente desatualizada.
- A arquitetura passa a exigir observabilidade de lag, reprocessamento e reconciliação.
- A adoção de CQRS não exige event sourcing: a base de publicação continua sendo persistida normalmente, e os eventos alimentam a projeção.
