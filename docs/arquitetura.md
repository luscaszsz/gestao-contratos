# Arquitetura da Aplicação — Gestão de Contratos

## 1. Componentes

- **`publica-contrato`**: recebe solicitações de publicação, persiste os dados no PostgreSQL de publicação, envia solicitações de validação ao Kafka e consome os resultados.
- **`valida-contrato`**: consome solicitações de validação pelo Kafka, aplica as regras de validação e publica o resultado no Kafka.
- **`consulta-contratos`**: atende consultas usando seu próprio PostgreSQL e Redis como cache.
- **Kafka**: transporta as mensagens assíncronas entre os serviços de publicação e validação.
- **PostgreSQL de publicação**: armazena os dados pertencentes ao serviço de publicação.
- **PostgreSQL de consulta**: armazena os dados utilizados pelo serviço de consulta.
- **Redis**: armazena temporariamente resultados de consulta em cache.

As bases PostgreSQL são separadas. O Redis pertence ao fluxo de consulta e não participa diretamente da publicação ou da validação.

## 2. Fluxo de publicação e validação

1. O cliente envia uma solicitação HTTP para `publica-contrato`.
2. `publica-contrato` persiste os dados no PostgreSQL de publicação.
3. `publica-contrato` publica uma mensagem no tópico Kafka `contrato.validacao.solicitada`.
4. `valida-contrato` consome a mensagem e executa as regras de validação.
5. `valida-contrato` publica o resultado no tópico Kafka `contrato.validacao.resultado`.
6. `publica-contrato` consome o resultado e atualiza o estado correspondente, conforme a implementação do serviço.

### Representação textual

```text
Cliente
  |
  | Requisição HTTP
  v
publica-contrato
  |                         \
  | Persiste dados           \ Publica solicitação
  v                            v
PostgreSQL de publicação     Kafka: contrato.validacao.solicitada
                                |
                                | Consumo da solicitação
                                v
                            valida-contrato
                                |
                                | Publica resultado
                                v
                            Kafka: contrato.validacao.resultado
                                |
                                | Consumo do resultado
                                v
                            publica-contrato
                                |
                                | Atualiza estado conforme implementação
                                v
                     PostgreSQL de publicação
```

O fluxo é assíncrono: a publicação da solicitação de validação não significa que a validação já terminou.

## 3. Fluxo de consulta

1. O cliente envia uma requisição HTTP para `consulta-contratos`.
2. O serviço procura o resultado no Redis.
3. **Cache hit:** se a chave existir, o serviço retorna o valor armazenado em cache.
4. **Cache miss:** se a chave não existir, o serviço consulta o PostgreSQL de consulta.
5. Após obter o resultado do banco, o serviço armazena o valor no Redis, conforme a política de cache configurada, e retorna a resposta ao cliente.

### Representação textual

```text
Cliente
  |
  | Requisição HTTP
  v
consulta-contratos
  |
  | Procura a chave
  v
Redis
  |                         \
  | Cache hit                \ Cache miss
  v                            v
Retorna resposta           PostgreSQL de consulta
                                |
                                | Retorna dados
                                v
                         consulta-contratos
                                |
                                | Armazena no Redis
                                v
                         Retorna resposta ao cliente
```

O Redis é uma camada de cache; o PostgreSQL de consulta é a persistência do serviço. O cache hit ou cache miss não dispara publicação de contrato nem participa do fluxo Kafka.

## 4. Separação entre os fluxos

No estado atual, o fluxo de consulta é independente do fluxo de publicação e validação:

- `publica-contrato` e `valida-contrato` comunicam-se por Kafka.
- `consulta-contratos` utiliza seu próprio PostgreSQL e Redis.
- Não há, na arquitetura atual documentada, sincronização automática entre o PostgreSQL de publicação e o PostgreSQL de consulta.

## 5. Evolução futura: tombamento para consulta

Como evolução futura, os dados de publicação poderão alimentar o modelo de leitura por eventos Kafka. Nesse cenário, um consumidor/projetor atualizará o PostgreSQL de consulta, e o Redis continuará funcionando apenas como cache.

Esse fluxo futuro não está representado como funcionalidade existente:

```text
publica-contrato
  |
  | Evento de negócio
  v
Kafka
  |
  | Consumo do evento
  v
Projetor de consulta (futuro)
  |
  | Atualiza projeção
  v
PostgreSQL de consulta
  |
  v
Redis (cache)
```

