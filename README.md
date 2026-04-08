# lancamento-bancario

Processamento diário de encargos em conta corrente. O sistema lê um arquivo de lançamentos, valida cada um via comunicação assíncrona com o sistema de contas e persiste o resultado.

## Stack

Java 17 · Spring Boot 4 · Spring Batch · Kafka (KRaft) · PostgreSQL · Docker

## Arquitetura

Hexagonal (Ports and Adapters). O domínio não conhece Spring, Kafka ou JPA — toda dependência de infraestrutura fica nos adapters.

```
domain/ports/in    → contratos que o sistema expõe
domain/ports/out   → contratos que o sistema consome
application/       → implementações dos casos de uso
adapters/in        → REST, Kafka consumers, Spring Batch
adapters/out       → Kafka producers, JPA
```

## Fluxo

```
Arquivo CSV
  → Spring Batch (chunks de 1000)
  → Persiste como PENDENTE + publica evento "solicitar-status-conta"
  → Sistema de Contas responde com status
  → ATIVA             → PROCESSADO + publica "lancamento-contabil"
  → CANCELADA         → RECUSADO
  → BLOQUEIO_JUDICIAL → RECUSADO
  → Disponível via API REST
```

## Formato do arquivo

```
lancamentoId,contaId,valor,tipo,dataHoraArquivo
550e8400-...,123,1500.75,DEBITO,2026-04-07T04:00:00
```

## Idempotência

Mesma mensagem processada duas vezes não gera duplicidade. Garantido por:
- Verificação prévia por `lancamentoId` (performance)
- `UNIQUE constraint` no banco (corretude)
- Captura de `DataIntegrityViolationException` para race conditions

## Resiliência

### Falha na publicação do evento após persistência

Existe um gap entre salvar o lançamento no banco e publicar o evento no Kafka.
Em caso de falha do broker após o `saveAll`, lançamentos podem ficar indefinidamente como `PENDENTE`.

Soluções previstas para produção:

- **Outbox Pattern** — salvar o evento numa tabela `outbox` na mesma transação do lançamento.
  Um processo separado lê e publica no Kafka, garantindo entrega mesmo em caso de falha.
- **Reprocessamento por status** — job periódico que republica eventos de lançamentos
  `PENDENTE` há mais tempo que o esperado.
- **Retry + Dead Letter Queue** — configurar retry no producer Kafka com DLQ para
  mensagens que não conseguiram ser entregues.

## Performance

Testado localmente com 20k registros

Para atender o SLA de produção (20M em 2h), poderiam ser implementados:
- Spring Batch com particionamento do arquivo
- Múltiplas partições Kafka + consumers paralelos
- Horizontal scaling da aplicação

## API

### Exemplos

```bash
# buscar lançamento por ID
curl "http://localhost:8080/lancamentos/550e8400-e29b-41d4-a716-446655440000"

# buscar lançamentos de uma conta
curl "http://localhost:8080/lancamentos?contaId=123&page=0&size=20"

# segunda página
curl "http://localhost:8080/lancamentos?contaId=123&page=1&size=20"

# disparar processamento manual
curl -X POST "http://localhost:8080/lancamentos/arquivo?caminho=/app/arquivos/lancamentos.csv"
```

## Agendamento

Processamento automático todo dia às 04:00 via `@Scheduled`. O caminho do arquivo é configurável:

```properties
ingestao.arquivo.caminho=/app/arquivos/lancamentos.csv
```

## Como rodar

```bash
./mvnw clean package
docker-compose up --build
```

Um arquivo de exemplo já está disponível em `./arquivos/lancamentos.csv`. Para disparar o processamento manualmente:

```bash
curl -X POST "http://localhost:8080/lancamentos/arquivo?caminho=/app/arquivos/lancamentos.csv"
```

Ou aguarde o agendamento automático às 04:00.

## Dev

No profile `dev` sobe um mock do Sistema de Contas que simula os status por contaId (ATIVA, CANCELADA, BLOQUEIO_JUDICIAL).

```properties
spring.profiles.active=dev
```

## Autor

Paulo Aguiarti — github.com/paguiarti
