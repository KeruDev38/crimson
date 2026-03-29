# API Examples

These examples help you quickly exercise the current Crimson API once the stack is running.

## Create a customer

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ava",
    "lastName": "Mercer",
    "email": "ava.mercer@crimson.test"
  }'
```

## Create an account

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "<customer-id>",
    "currency": "MXN",
    "initialBalance": 2500.00
  }'
```

## Get an account

```bash
curl http://localhost:8080/api/v1/accounts/<account-id>
```

## List account transactions

```bash
curl "http://localhost:8080/api/v1/accounts/<account-id>/transactions?page=0&size=20"
```

## Transfer funds

```bash
curl -X POST http://localhost:8080/api/v1/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "senderAccountId": "<sender-account-id>",
    "receiverAccountId": "<receiver-account-id>",
    "amount": 150.00,
    "currency": "MXN",
    "reference": "Operations transfer"
  }'
```

## Dashboard

Open:

```text
http://localhost:3000
```

Use the account UUID from the create-account response in the dashboard lookup field to populate the metrics view.
For the guided two-step flow, open `http://localhost:3000/onboarding`.
