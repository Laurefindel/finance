
---

```markdown
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

```