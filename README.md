# quarkus-validation-example

Payment transaction validation system built with **Quarkus**, following **Clean Architecture** (Hexagonal Architecture) principles.

## Tech Stack

| Component     | Technology                                   |
| ------------- | -------------------------------------------- |
| Runtime       | Java 21 + Quarkus 3.17                       |
| API           | MicroProfile GraphQL (SmallRye)              |
| ORM           | Hibernate ORM with Panache                   |
| Database      | PostgreSQL 14                                |
| Migrations    | Flyway                                       |
| Messaging     | Apache Kafka + SmallRye Reactive Messaging   |
| Serialization | Jackson (JSON)                               |
| Validation    | Hibernate Validator                          |
| Health        | SmallRye Health                              |
| Tests         | JUnit 5 + Mockito + Testcontainers + AssertJ |
| Linting       | Checkstyle                                   |
| CI            | GitHub Actions                               |
| Containers    | Docker + Docker Compose                      |

---

## Architecture

Both microservices follow **Clean Architecture** (Ports & Adapters):

```
src/main/java/com/example/<service>/
├── domain/                   # Pure domain — no framework dependencies
│   ├── model/                # Entities and value objects
│   └── port/
│       ├── in/               # Input ports (use case interfaces)
│       └── out/              # Output ports (repository / event interfaces)
├── application/
│   └── service/              # Application services (implements use cases)
└── infrastructure/
    └── adapter/
        ├── in/               # Inbound adapters (GraphQL, Kafka consumers)
        └── out/              # Outbound adapters (Panache repos, Kafka producers)
```

### Microservices

#### ms-payments (port 3000)

- Exposes a **GraphQL API** to create and retrieve transactions
- Persists transactions in PostgreSQL via **Hibernate ORM with Panache**
- Publishes `transaction-validation-request` events to Kafka
- Consumes `transaction-validation-response` events to update transaction status

#### ms-frauds (port 3001 → internal 8080)

- Consumes `transaction-validation-request` events from Kafka
- Applies fraud rules: **value > 1000 → REJECTED**, otherwise **APPROVED**
- Publishes `transaction-validation-response` events back to Kafka

---

## Quick Start

### Prerequisites

- Docker + Docker Compose
- Make
- Java 21 (for local development)
- Maven 3.9+

### Start everything

```bash
# Create network + start infra + register schemas + create topics + start services
make me-happy
```

### Stop everything

```bash
make me-down
```

### Step by step

```bash
# 1. Create Docker network, Start Postgres, Kafka, Zookeeper, Schema Registry and registry schema/topics
make me-happy

# 2. Build and start microservices
make services-up
```

---

## Development

### Run locally (without Docker for services)

```bash
# Start infrastructure only
make infra-up
make schema
make kafka-topics

# Run ms-payments in dev mode (live reload)
cd ms-payments
mvn quarkus:dev

# Run ms-frauds in dev mode (separate terminal)
cd ms-frauds
mvn quarkus:dev -Dquarkus.http.port=8082
```

Quarkus Dev Services will automatically start **PostgreSQL** and **Kafka** via Testcontainers when running tests.

---

## Testing

```bash
# Run all tests
make test-all

# Or per service
make test-payments
make test-frauds
```

Tests use **Quarkus Dev Services** (Testcontainers) — no manual infrastructure setup needed.

---

## Linting

```bash
# Lint all services
make lint-all

# Or per service
make lint-payments
make lint-frauds
```

---

## GraphQL API (ms-payments)

GraphQL UI available at: `http://localhost:8080/q/graphql-ui`

### Create a transaction

```graphql
mutation {
  createTransaction(
    createTransactionInput: {
      accountExternalIdDebit: "550e8400-e29b-41d4-a716-446655440000"
      accountExternalIdCredit: "550e8400-e29b-41d4-a716-446655440001"
      transferTypeId: 1
      value: 500.00
    }
  ) {
    transactionExternalId
    transactionType {
      name
    }
    transactionStatus {
      name
    }
    value
    createdAt
  }
}
```

### Retrieve a transaction

```graphql
{
  retrieveTransaction(externalId: "your-uuid-here") {
    transactionExternalId
    transactionType {
      name
    }
    transactionStatus {
      name
    }
    value
    createdAt
    updatedAt
  }
}
```

---

## Health Checks

- ms-payments: `http://localhost:8080/q/health`
- ms-frauds: `http://localhost:8081/q/health`

---

## Kafka Topics

| Topic                             | Producer    | Consumer    |
| --------------------------------- | ----------- | ----------- |
| `transaction-validation-request`  | ms-payments | ms-frauds   |
| `transaction-validation-response` | ms-frauds   | ms-payments |

---

## Database Migrations

Flyway runs automatically at startup. Migration scripts are in:
`ms-payments/src/main/resources/db/migration/`

| Migration | Description                                                                 |
| --------- | --------------------------------------------------------------------------- |
| V1        | Create transaction, transaction_type, transaction_status tables + seed data |
