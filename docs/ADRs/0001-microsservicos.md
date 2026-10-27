# ADR 0001 — Separação em microsserviços por responsabilidade

- **Status:** Aceita
- **Data:** 2026-10-26

## Contexto

A aplicação está organizada em serviços com responsabilidades distintas: `publica-contrato`, `valida-contrato` e `consulta-contratos`. O fluxo de publicação e validação é orientado a eventos; a consulta possui persistência e cache próprios e, no estado atual, não participa desse fluxo.

## Decisão

Manter os componentes separados por responsabilidade de negócio e integração:

- **`publica-contrato`**: recebe solicitações de publicação, persiste os dados de publicação e publica solicitações de validação; também consome os resultados de validação.
- **`valida-contrato`**: consome solicitações, aplica as regras de validação e publica o resultado.
- **`consulta-contratos`**: atende consultas usando sua própria base PostgreSQL e Redis como cache; permanece independente do fluxo de publicação enquanto não houver integração explícita.

Os serviços podem ser implantados e evoluídos separadamente, respeitando contratos de API e de eventos versionados.

## Consequências

### Positivas
- Isolamento de responsabilidades e possibilidade de escalar cada serviço conforme sua carga.
- Menor acoplamento em tempo de execução entre publicação e validação.
- A consulta pode otimizar seu modelo de leitura sem alterar diretamente o armazenamento de publicação.

### Custos e riscos
- Mais componentes para operar, monitorar, configurar e testar.
- Falhas de rede e processamento parcial passam a fazer parte do desenho.
- É necessário definir observabilidade, timeouts, retries, idempotência e tratamento de mensagens problemáticas.

## Restrições

- Um serviço não deve acessar diretamente as tabelas ou o banco de outro serviço.
- Não criar uma chamada síncrona entre serviços para contornar um contrato de evento quando a operação puder seguir o fluxo assíncrono existente.
- Não presumir que `consulta-contratos` esteja sincronizado com `publica-contrato` até que a integração futura seja implementada.
