# Crimson

Crimson is a multi-module bank transaction platform with a Spring Boot backend and a Next.js frontend dashboard.

The current stack supports:

- Customer creation
- Account creation
- Account lookup
- Transaction history lookup
- Fund transfers between accounts
- A frontend dashboard with account metrics and operational forms

## Modules

- `launcher`: Spring Boot application bootstrap, runtime config, Flyway migrations
- `core-transactions`: domain, JPA entities, repositories, use cases, application services
- `api-rest`: REST controllers, transport DTOs, API error handling
- `notifications`: placeholder module for future event-driven workflows
- `frontend`: Next.js App Router dashboard

## Architecture

The backend follows a simple modular structure:

- `api-rest` handles HTTP
- `core-transactions` contains business logic and persistence
- `launcher` assembles the application and runs Flyway

The frontend does not call the Java API directly from the browser. It uses Next.js route handlers as a proxy layer, which keeps the backend URL configurable and works cleanly in Docker.

## Prerequisites

For the easiest path:

- Docker
- Docker Compose

For running without Docker:

- Java 17
- Node.js 20+
- npm
- PostgreSQL 17 or compatible

## Quick Start With Docker

1. Build and start everything:

```bash
docker compose up --build
```

2. Open the applications:

- Frontend dashboard: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

3. Stop the stack:

```bash
docker compose down
```

To remove the database volume too:

```bash
docker compose down -v
```

## Local Development

### Backend

Start PostgreSQL first, then run:

```bash
./gradlew :launcher:bootRun
```

The backend reads these environment variables:

- `CRIMSON_DATASOURCE_URL`
- `CRIMSON_DATASOURCE_USERNAME`
- `CRIMSON_DATASOURCE_PASSWORD`

Default local values are already defined in [application.yaml](/home/keru/Dev/crimson/launcher/src/main/resources/application.yaml).

### Frontend

Inside `frontend/`:

```bash
npm install
npm run dev
```

The frontend runs at `http://localhost:3000`.

If needed, copy the example env file:

```bash
cp frontend/.env.example frontend/.env.local
```

## Main Endpoints

### Create customer

`POST /api/v1/customers`

Example body:

```json
{
  "firstName": "Ava",
  "lastName": "Mercer",
  "email": "ava.mercer@crimson.test"
}
```

### Create account

`POST /api/v1/accounts`

Example body:

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "currency": "MXN",
  "initialBalance": 2500.00
}
```

### Get account

`GET /api/v1/accounts/{accountId}`

### List account transactions

`GET /api/v1/accounts/{accountId}/transactions?page=0&size=20`

### Transfer funds

`POST /api/v1/transfers`

Example body:

```json
{
  "senderAccountId": "11111111-1111-1111-1111-111111111111",
  "receiverAccountId": "22222222-2222-2222-2222-222222222222",
  "amount": 150.00,
  "currency": "MXN",
  "reference": "Operations transfer"
}
```

## Frontend Experience

The dashboard includes:

- Account inspection by UUID
- Account balance and activity metrics
- Existing-customer account opening form
- Transfer submission form
- Recent transaction feed
- A dedicated onboarding screen for customer creation and account provisioning

The visual style uses crimson-ruby tones with a more youthful fintech feel while staying professional.

## Files That Matter First

- [README.md](/home/keru/Dev/crimson/README.md)
- [compose.yaml](/home/keru/Dev/crimson/compose.yaml)
- [launcher/src/main/resources/application.yaml](/home/keru/Dev/crimson/launcher/src/main/resources/application.yaml)
- [launcher/src/main/resources/db/migration/V1__create_bank_schema.sql](/home/keru/Dev/crimson/launcher/src/main/resources/db/migration/V1__create_bank_schema.sql)
- [frontend/app/page.tsx](/home/keru/Dev/crimson/frontend/app/page.tsx)
- [api-rest/src/main/java/com/enigma/crimson/api/accounts/AccountsController.java](/home/keru/Dev/crimson/api-rest/src/main/java/com/enigma/crimson/api/accounts/AccountsController.java)
- [api-rest/src/main/java/com/enigma/crimson/api/transactions/TransactionsController.java](/home/keru/Dev/crimson/api-rest/src/main/java/com/enigma/crimson/api/transactions/TransactionsController.java)

## Helpful Commands

Run backend tests:

```bash
./gradlew test
```

Build backend runnable jar:

```bash
./gradlew :launcher:bootJar
```

Check Docker Compose config:

```bash
docker compose config
```

## Notes

- Flyway manages the database schema on startup.
- The frontend currently expects the backend to be reachable and does not yet include authentication.
- `frontend/package.json` currently uses `latest` for Next.js and React. If you want stricter reproducibility, the next step should be pinning exact versions and committing a lockfile.
