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
