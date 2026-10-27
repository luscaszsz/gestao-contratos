# Auto-avaliação — gestão de contratos
Grupo: Lucas - arquitetura e desenvolvimento
Tema/domínio: Aplicação em 3 módulos de gestão de contratos de trabalho. 
Os contratos podem ser publicados, validados e consultados em cada módulo.
Perfil de execução: A   ·  Fallbacks usados: estratégias de retry em PublicaContratoService.java para comuncação com validacao-contrato.
## Evidências por critério
1. Decomposição de domínio — nível auto-atribuído: 2
   Evidência: docs/arquitetura.md e ADRs definem bem os domínios, mas falta linguagem ubíqua e agregados.
2. Comunicação assíncrona — 3
   Evidência: Comunicação entre api de validação e publicação, com estratégia de retry em PublicaContratoService.java
3. Idempotência e consistência — 3
   Evidência: chave é testada em PublicaContratoService.java para que não seja reprocessada por api de validação em banco próprio.
4. Cache — 3 / 5. Resiliência — 2 / 6. Testabilidade — 0
   Evidência: Não incluí testabilidade. cache implementado em ConsultaContratoService
7. Decisões arquiteturais — 3  → ver docs/adr/ --> busquei implementar serviços separados de consulta, validação e publicação.
8. Uso crítico de IA — 3
   Como usamos IA e o que validamos manualmente: Busquei estruturar o projeto junto com IA, 
indicando quais decisões buscava para a api e no processo de implementação, avaliando se as decisões tomadas foram no caminho esperado.
9. Execução — 3
   Como rodar: ./mvnw clean package; docker compose up -d --build --force-recreate (com docker desktop)
