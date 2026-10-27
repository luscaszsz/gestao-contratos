# Gestão de Contratos

Aplicação de demonstração de uma arquitetura de microsserviços com
**Quarkus**, **Apache Kafka**, **PostgreSQL**, **Redis** e **Docker
Compose**. O fluxo principal publica contratos para validação por meio
de eventos e publica o resultado da validação em outro tópico Kafka.

## Arquitetura

### Serviços principais

  ---------------------------------------------------------------------------------
  Serviço              Responsabilidade          Porta no host   Porta no container
  -------------------- ------------------ -------------------- --------------------
  `publica-contrato`   Recebe                           `8082`               `8080`
                       solicitações de                         
                       publicação,                             
                       persiste os dados                       
                       de publicação e                         
                       envia eventos de                        
                       validação ao                            
                       Kafka. Também                           
                       consome os                              
                       resultados da                           
                       validação. Se necessario, 
                       atualiza publicacao. 

  `valida-contrato`    Consome                          `8081`               `8080`
                       solicitações de                         
                       validação, aplica                       
                       as regras de                            
                       demonstração e                          
                       publica o                               
                       resultado da                            
                       validação.                              
  ---------------------------------------------------------------------------------

### Infraestrutura

  -------------------------------------------------------------------------
  Componente            Imagem                                Porta no host
  --------------------- ---------------------- ----------------------------
  Apache Kafka          `apache/kafka:3.9.1`                         `9092`

  PostgreSQL da         `postgres:17`               `5432` (se publicado no
  publicação                                                       Compose)

  PostgreSQL da         `postgres:17`                              Conforme
  consulta auxiliar                                    `docker-compose.yml`

  Redis                 `redis:7-alpine`            `6379` (se publicado no
                                                                   Compose)
  -------------------------------------------------------------------------

O projeto também contém o serviço `consulta-contratos`, com PostgreSQL e
Redis. Ele é um componente auxiliar e **não faz parte do fluxo de
eventos de publicação e validação** descrito abaixo.

## Fluxo de publicação e validação


1.  Um cliente envia uma solicitação ao serviço `publica-contrato`.
2.  O serviço registra os dados relacionados à publicação no PostgreSQL
    correspondente e publica uma mensagem no tópico
    `contrato.validacao.solicitada`.
3.  O serviço `valida-contrato` consome a mensagem e avalia os dados do
    evento.
4.  O resultado é publicado no tópico `contrato.validacao.resultado`.
5.  O serviço `publica-contrato` consome o resultado usando o grupo
    `publica-contratos` e pode atualizar o estado associado à publicação
    conforme a implementação do serviço.

Os bancos de publicação e de consulta são separados. O cache da consulta
não participa da publicação nem da validação de contratos.

## Regras de validação de demonstração

O `ValidacaoService` do serviço `valida-contrato` aplica regras simples:

-   Reprova o evento quando o payload, o valor ou os dados necessários
    estão ausentes.
-   Reprova valores superiores a `10.000.000`.
-   Aprova os demais valores válidos.
-   Produz um evento de resultado com status, motivo, identificador do
    contrato e dados de correlação.

O serviço mantém também um mapa em memória indexado por `eventId` para
reutilizar resultados durante a vida do processo. Como esse mapa não é
persistente, ele não garante idempotência após reinicializações nem
coordenação entre múltiplas instâncias.

## Tópicos e grupos Kafka

  -----------------------------------------------------------------------
  Nome                                Uso
  ----------------------------------- -----------------------------------
  `contrato.validacao.solicitada`     Eventos enviados por
                                      `publica-contrato` e consumidos por
                                      `valida-contrato`.

  `contrato.validacao.resultado`      Resultados enviados por
                                      `valida-contrato` e consumidos por
                                      `publica-contrato`.

  `valida-contratos`                  Grupo consumidor de solicitações de
                                      validação.

  `publica-contratos`                 Grupo consumidor dos resultados de
                                      validação.
  -----------------------------------------------------------------------

Dentro da rede Docker, os serviços usam `kafka:29092`. Para ferramentas
executadas diretamente na máquina host, use `localhost:9092`.

## Tecnologias

-   Java e Quarkus 3.36.0
-   SmallRye Reactive Messaging / Kafka
-   Apache Kafka 3.9.1 em modo KRaft
-   PostgreSQL 17
-   Redis 7
-   Hibernate ORM com Panache
-   Docker e Docker Compose
-   Maven Wrapper

## Pré-requisitos

-   Docker Desktop instalado e em execução.
-   Docker Compose disponível pelo comando `docker compose`.
-   Git, caso vá clonar o repositório.
-   Java e Maven não são necessários para executar as imagens já
    construídas. São necessários para compilar ou desenvolver
    localmente, conforme o fluxo de build do projeto.

## Como executar

Na raiz do projeto, onde está o `docker-compose.yml`:

``` powershell
docker compose up -d --build
```

Verifique o estado dos containers:

``` powershell
docker compose ps
```

Acompanhe os logs:

``` powershell
docker compose logs -f kafka
docker compose logs -f valida-contrato
docker compose logs -f publica-contrato
```

Para acompanhar todos os serviços:

``` powershell
docker compose logs -f
```

Para encerrar os containers sem remover os volumes persistentes:

``` powershell
docker compose down
```

> Evite `docker compose down -v` se quiser preservar os dados
> armazenados nos volumes.

## Configuração

As aplicações usam variáveis de ambiente para configurar as conexões. Os
valores padrão abaixo vêm das configurações conhecidas dos serviços;
confira o `docker-compose.yml` para os valores efetivamente definidos no
seu ambiente.

### Kafka

``` properties
kafka.bootstrap.servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

No Compose, `KAFKA_BOOTSTRAP_SERVERS` deve apontar para `kafka:29092`.

### `publica-contrato`

Variáveis relacionadas ao PostgreSQL:

-   `DB_HOST`
-   `DB_PORT`
-   `DB_NAME`
-   `DB_USERNAME`
-   `DB_PASSWORD`

O Compose deve fornecer os valores internos do container, incluindo o
nome do serviço PostgreSQL correto e o banco `publicacao_db`, conforme a
configuração atual.

### `valida-contrato`

O serviço usa os tópicos Kafka definidos acima e o grupo consumidor
`valida-contratos`. Não depende de acesso ao PostgreSQL para executar as
regras de validação mostradas no `ValidacaoService`.

## Verificação rápida do Kafka

Listar os tópicos existentes:

``` powershell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

Consultar o grupo consumidor de publicação:

``` powershell
docker compose exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group publica-contratos
```

Consultar o estado do quorum KRaft:

``` powershell
docker compose exec kafka /opt/kafka/bin/kafka-metadata-quorum.sh --bootstrap-server localhost:9092 describe --status
```

Se os grupos perderem membros repetidamente, procure nos logs mensagens
de `heartbeat expiration`, `rebalance`, `CommitFailedException` e falhas
de conexão. O fato de o quorum estar ativo não garante, por si só, que a
comunicação esteja estável em todos os momentos.

## API HTTP

As portas mapeadas no Compose são:

-   `http://localhost:8082` --- `publica-contrato`
-   `http://localhost:8081` --- `valida-contrato`
-   `http://localhost:8080` --- `consulta-contratos` (componente
    auxiliar)

Os caminhos e métodos HTTP disponíveis dependem dos recursos REST
implementados. Consulte as classes anotadas com `@Path` para obter a
lista exata de endpoints; este README não presume rotas de publicação ou
validação que não foram confirmadas.

## Consulta auxiliar, PostgreSQL e Redis

O serviço `consulta-contratos` é isolado do fluxo de eventos descrito
anteriormente. Ele utiliza uma base PostgreSQL própria e Redis para
cachear resultados de consulta. O cache não dispara publicação de
contrato nem participa da validação.

A intenção é que, no futuro, haja tombamento dos dados publicados pelo fluxo
de publicação na base de consulta por meio do padrão CQRS, que fugia do escopo
do trabalho. Portanto, a consulta existe apenas para demonstração do redis
com bases apartadas.

## Estrutura lógica

A estrutura exata de diretórios depende do repositório, mas os
componentes principais são:

-   `publica-contrato`: API de publicação, persistência e consumo dos
    resultados de validação.
-   `valida-contrato`: consumo de solicitações, aplicação das regras e
    publicação do resultado.
-   `consulta-contratos`: serviço auxiliar de consulta com cache Redis.
-   `docker-compose.yml`: definição dos serviços e da rede local.

