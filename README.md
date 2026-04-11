
---

# Finance API

**Finance API** is a specialized RESTful backend application designed to manage and control personal or business financial transactions.  
The application provides a structured, secure, and scalable way to store, retrieve, and filter users, accounts, currencies, and financial operations.

---

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.3**
- **PostgreSQL**
- **Maven**

---

## Architecture

The application follows a layered architecture pattern:

```
Controller → Service → Repository → Database
```

### Key principles:
- Controllers handle HTTP requests and responses
- Services contain business logic
- Repositories manage persistence
- DTOs ensure database entities are never exposed directly

---

# Financial Operations

A financial operation represents a transfer of funds between two accounts.  
Each operation is always associated with a currency and affects account balances.

---

## FinancialOperationResponseDto

```json
{
  "id": 1,
  "senderAccountId": 10,
  "receiverAccountId": 15,
  "description": "Payment for services",
  "amount": 250.00,
  "currencyCode": "USD"
}
````

---

## Create Financial Operation

### POST `/operations`

Creates a new financial operation and updates account balances atomically.

### Request Body (`FinancialOperationRequestDto`)

```json
{
  "senderAccountId": 10,
  "receiverAccountId": 15,
  "amount": 250.00,
  "description": "Payment for services"
}
```

### Response `200 OK`

```json
{
  "id": 1,
  "senderAccountId": 10,
  "receiverAccountId": 15,
  "description": "Payment for services",
  "amount": 250.00,
  "currencyCode": "USD"
}
```

---

## Get All Financial Operations

### GET `/operations`

Returns a list of all financial operations.

### Response `200 OK`

```json
[
  {
    "id": 1,
    "senderAccountId": 10,
    "receiverAccountId": 15,
    "description": "Payment for services",
    "amount": 250.00,
    "currencyCode": "USD"
  }
]
```

---

## Get Operations by Sender User

### GET `/operations?senderUserId={userId}`

Returns all operations initiated by a specific user.

Example:

```
GET /operations?senderUserId=3
```

---

## Get Operation by ID

### GET `/operations/{id}`

Example:

```
GET /operations/1
```

---

## Delete Financial Operation

### DELETE `/operations/{id}`

Deletes a financial operation by its identifier.

> ⚠️ Account balances are **not reverted**.
> Intended for administrative or cleanup purposes.

---

# Accounts

An account belongs to a user and is always associated with a single currency.

---

## AccountResponseDto

```json
{
  "id": 10,
  "balance": 1250.00,
  "currency": {
    "id": 1,
    "code": "USD",
    "name": "US Dollar"
  },
  "user": {
    "id": 3,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@mail.com"
  },
  "outcomingOperations": [],
  "incomingOperations": []
}
```

---

## Create Account

### POST `/accounts`

```json
{
  "userId": 3,
  "currencyCode": "USD"
}
```

---

## Get Accounts

### GET `/accounts`

Supports optional filtering by:

* `userId`
* `currency`

Examples:

```
GET /accounts
GET /accounts?userId=3
GET /accounts?currency=USD
GET /accounts?userId=3&currency=USD
```

---

## Replenish Account Balance

### PATCH `/accounts/{id}/replenish/{amount}`

Example:

```
PATCH /accounts/10/replenish/500.00
```

---

# Currencies

---

## CurrencyResponseDto

```json
{
  "id": 1,
  "code": "USD",
  "name": "US Dollar"
}
```

---

## Currency Endpoints

| Method | Endpoint                          | Description               |
| ------ | --------------------------------- | ------------------------- |
| GET    | `/currencies`                     | Get all currencies        |
| GET    | `/currencies/{id}`                | Get currency by ID        |
| GET    | `/currencies/by-code?code=USD`    | Get currency by code      |
| GET    | `/currencies/by-name?name=Dollar` | Search currencies by name |
| POST   | `/currencies`                     | Create new currency       |
| PUT    | `/currencies/{id}`                | Full update               |
| PATCH  | `/currencies/{id}`                | Partial update            |
| DELETE | `/currencies/{id}`                | Delete currency           |

---

# Users

---

## UserResponseDto

```json
{
  "id": 3,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@mail.com",
  "status": "ACTIVE",
  "accountsIds": [10, 11],
  "roleIds": [1]
}
```

---

## User Endpoints

| Method | Endpoint                              | Description             |
| ------ | ------------------------------------- | ----------------------- |
| GET    | `/users/all`                          | Get all users           |
| GET    | `/users/{id}`                         | Get user by ID          |
| POST   | `/users/register`                     | Register new user       |
| PATCH  | `/users/{id}/change-user-information` | Update user information |
| DELETE | `/users/{id}`                         | Delete user             |

---

# Roles

---

## RoleDto

```json
{
  "name": "ROLE_ADMIN"
}
```

---

## Role Endpoints

| Method | Endpoint                        |
| ------ | ------------------------------- |
| GET    | `/roles`                        |
| GET    | `/roles/{id}`                   |
| GET    | `/roles/by-name?name=ROLE_USER` |
| POST   | `/roles`                        |
| DELETE | `/roles/{id}`                   |

---

## Code Quality & Static Analysis

The project follows industry best practices and is continuously analyzed using:

* SonarCloud
* SonarLint
* Checkstyle

🔗 [https://sonarcloud.io/summary/new_code?id=Laurefindel_finance&branch=main](https://sonarcloud.io/summary/new_code?id=Laurefindel_finance&branch=main)

---

## Async Business Operation (Task ID + Status)

Implemented endpoints:

| Method | Endpoint                   | Description                              |
| ------ | -------------------------- | ---------------------------------------- |
| POST   | `/async/replenish`         | Starts async replenish and returns taskId |
| GET    | `/async/replenish/{taskId}`| Returns task status (`PENDING/RUNNING/SUCCESS/FAILED`) |
| GET    | `/async/replenish/metrics` | Returns atomic task counters             |

The async worker uses `@Async` + `CompletableFuture` and has configurable delay:

```yaml
app:
  async:
    replenish-delay-ms: 15000
```

This delay makes status polling observable (task is not completed instantly).

---

## Thread Safety: Atomic Counter

`AsyncTaskCounterService` uses `AtomicLong` counters:

- submitted
- running
- succeeded
- failed

These counters are safe under concurrent access and exposed via `/async/replenish/metrics`.

---

## Race Condition Demo (50+ Threads)

Endpoint:

| Method | Endpoint                 | Description |
| ------ | ------------------------ | ----------- |
| GET    | `/concurrency/race-demo` | Runs concurrency demo using `ExecutorService` |

Query params:

- `threads` (default `64`, must be `>= 50`)
- `incrementsPerThread` (default `10000`)

The demo compares:

- unsafe counter (no synchronization) → race condition
- synchronized counter (`synchronized`) → correct result
- atomic counter (`AtomicInteger`) → correct result

---

## JMeter Load Testing

JMeter test plan:

- `jmeter/async-metrics-load-test.jmx`

Scenario:

- 100 threads
- ramp-up 10s
- 20 loops per thread
- endpoint `GET /async/replenish/metrics`
- total samples: 2000

### Run command

```bash
jmeter -n \
  -t jmeter/async-metrics-load-test.jmx \
  -l jmeter/results/results.jtl \
  -j jmeter/results/summary.log \
  -e -o jmeter/results/dashboard
```

### Results (run on 2026-04-10)

- Samples: `2000`
- Errors: `0` (`0.00%`)
- Mean response time: `1.521 ms`
- Median response time: `1 ms`
- Max response time: `264 ms`
- Throughput: `204.67 req/s`
- p90/p95/p99 (JMeter `pct1/pct2/pct3`): `3 / 3 / 5 ms`

Artifacts:

- `jmeter/results/results.jtl`
- `jmeter/results/summary.log`
- `jmeter/results/dashboard/index.html`

---

## Docker

### Environment variables

Copy and edit:

```bash
cp .env.example .env
```

Used variables:

- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `DB_PORT`
- `APP_PORT`
- `JAVA_OPTS`

### Run application + PostgreSQL

```bash
docker compose up -d --build
```

Health endpoint:

- `http://localhost:8080/actuator/health`

---

## PaaS deployment (Heroku)

1. Create Heroku app.
2. In Heroku app settings, add config vars:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
3. Set app health endpoint to `/actuator/health` (used in CI validation).

> For database, you can use Heroku Postgres or external PostgreSQL provider.

---

## GitHub CI/CD

Workflow file:

- `.github/workflows/ci-cd.yml`

Pipeline includes:

1. Maven build (`clean verify`)
2. Unit tests
3. Docker image build
4. Deployment to Heroku
5. Post-deploy healthcheck

### Required GitHub Secrets

- `HEROKU_API_KEY` — Heroku API key
- `HEROKU_APP_NAME` — Heroku app name
- `HEROKU_EMAIL` — email of Heroku account
- `APP_HEALTHCHECK_URL` — full URL to `/actuator/health` on deployed service