# ADR 0005 — Redis como cache da API de consulta

- **Status:** Aceita
- **Data:** 2026-10-26

## Contexto

O serviço `consulta-contratos` utiliza uma base PostgreSQL própria e Redis para cachear resultados. Esse serviço é auxiliar e não participa atualmente do fluxo de publicação/validação por Kafka.

## Decisão

Usar Redis como camada de cache de leitura da API de consulta, mantendo PostgreSQL como fonte persistente dos dados pertencentes ao serviço de consulta.

O padrão esperado é **cache-aside**:

1. Receber uma consulta e procurar a chave no Redis.
2. Em caso de cache hit, devolver o valor armazenado.
3. Em caso de cache miss, consultar o PostgreSQL, armazenar o resultado no Redis com TTL e devolver o resultado.
4. Tratar falhas do Redis sem perder a capacidade de consultar o banco, quando isso for aceitável para a operação.

## Consequências

### Positivas
- Reduz a latência e a carga no PostgreSQL para consultas repetidas.
- Permite definir TTL e invalidar chaves conforme a necessidade.

### Custos e riscos
- O cache pode conter dados expirados ou ficar inconsistente se a invalidação estiver incorreta.
- A chave deve incluir todos os parâmetros que alteram o resultado.
- Cache miss simultâneo pode gerar múltiplas consultas ao banco.

