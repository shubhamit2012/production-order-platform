# Production Order Platform

- `order-service` — Responsible for order management.
- `inventory-service` — Responsible for inventory reservation.
- `docs` — Architecture and design decisions.

## Running with Docker

Each service has its own `Dockerfile` (multi-stage Maven build), and the root `docker-compose.yml`
starts both services together with a PostgreSQL container.

```bash
docker compose up --build
```

| Container           | URL / Port                         |
|---------------------|------------------------------------|
| `order-service`     | http://localhost:8080              |
| `inventory-service` | http://localhost:8081              |
| `postgres`          | localhost:5433 (container: 5432)   |

- PostgreSQL hosts a single database, `production_order_db`, with one schema per service
  (`order_service`, `inventory_service`). Data persists in the `production-order-db-data` volume
  (`docker compose down -v` to reset).
- Each service has a single `application.properties` that defaults to in-memory H2 (PostgreSQL mode),
  so local runs and tests need no database. Compose overrides it with the PostgreSQL settings in
  each service's `.env` (`order-service/.env`, `inventory-service/.env`): `DB_URL`, `DB_USERNAME`,
  `DB_PASSWORD`, `H2_CONSOLE_ENABLED` (plus `INVENTORY_SERVICE_BASE_URL` for `order-service`).
- The PostgreSQL container's name / credentials / host port can be overridden with `DATABASE_NAME`,
  `DATABASE_USER`, `DATABASE_PASSWORD` and `POSTGRES_HOST_PORT`; keep the service `.env` files in sync.

## Database migrations

Schemas are managed by [Flyway](https://flywaydb.org/); Hibernate only validates (`ddl-auto=validate`).
Each service migrates its own schema on startup from `src/main/resources/db/migration`:

- `order-service` → `order_service` schema
- `inventory-service` → `inventory_service` schema (includes seed data in `V2__seed_inventory.sql`)

Add changes as new versioned files (e.g. `V3__add_column.sql`); never edit an applied migration.

## Logging

Both services use SLF4J with Spring Boot's console logging:

- `INFO`: Order creation and persistence (order ID and item count), plus inventory
  reservation start and prepared updates. Prepared updates are not a commit confirmation.
- `WARN`: Business rejections, unreadable request bodies, downstream retry attempts
  with backoff, and interrupted retries. The Order Service logs downstream service
  failures with their cause in its exception handler.
- `ERROR`: Unexpected failures with stack traces in the exception handlers.
- `DEBUG`: Downstream HTTP status and elapsed time (including retries), error-message
  fallback decisions, retry evaluation, and per-product reservation details.

New operational logs omit full request/response bodies, customer details, and credentials.
Enable detailed diagnostics per service with these environment variables:

```bash
LOGGING_LEVEL_COM_LEARNING_ORDERSERVICE=DEBUG
LOGGING_LEVEL_COM_LEARNING_INVENTORYSERVICE=DEBUG
```

## API error responses

Both services return JSON errors with the same shape:

```json
{
  "code": "INSUFFICIENT_INVENTORY",
  "message": "Insufficient inventory for the requested reservation"
}
```

| HTTP status | Code | Meaning |
|-------------|------|---------|
| 400 | `INVALID_INVENTORY` | Invalid inventory reservation |
| 400 | `INVALID_REQUEST` | Missing or malformed request body |
| 404 | `INVENTORY_NOT_FOUND` | No inventory exists for the requested product |
| 409 | `INSUFFICIENT_INVENTORY` | Inventory cannot fulfill the reservation |
| 503 | `INVENTORY_SERVICE_ERROR` | Order Service cannot complete the Inventory Service call |
| 500 | `INTERNAL_SERVER_ERROR` | Unexpected service failure |

Order Service preserves an Inventory Service business message only when the downstream
status and error code match (400 / `INVALID_INVENTORY`, 404 / `INVENTORY_NOT_FOUND`,
or 409 / `INSUFFICIENT_INVENTORY`)
and the message is nonblank. Missing, malformed, or unrecognized error bodies use a
generic business message. Downstream 404 responses are not retried. Other downstream HTTP failures and transport errors map to
503 after the existing retry policy completes. Technical failure details are logged
server-side, not returned in public 500 or 503 responses.

Run the service test suites from the repository root:

```bash
./order-service/mvnw -f order-service/pom.xml test
./inventory-service/mvnw -f inventory-service/pom.xml test
```
