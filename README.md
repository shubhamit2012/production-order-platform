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

## Local Chaos Monkey experiments

Inventory Service includes Chaos Monkey 4.0.0, Actuator, and the Spring Boot 4
AspectJ and RestClient starters. Container configuration lives in
`inventory-service/.env`, loaded by the single `docker-compose.yml`. The single
Inventory `application.properties` maps these environment values with safe defaults.
Start from the repository root:

```bash
podman compose up -d --build
```

The environment file selects the `chaos-monkey` profile and configures management
to listen on `0.0.0.0:8082` inside the container. Compose publishes management only
on the host's `127.0.0.1:8082`. The API remains on port 8081. Management controls
are unauthenticated and intended only for local development; do not expose or proxy
them to an untrusted network.

Only the service watcher is enabled, avoiding duplicate delays at the controller
and service layers. Injection starts disabled. To persist a five-second delay for
every service call, change these values in `inventory-service/.env`:

```dotenv
CHAOS_MONKEY_ENABLED=true
CHAOS_MONKEY_ASSAULTS_LEVEL=1
CHAOS_MONKEY_ASSAULTS_LATENCY_ACTIVE=true
CHAOS_MONKEY_ASSAULTS_LATENCY_RANGE_START=5000
CHAOS_MONKEY_ASSAULTS_LATENCY_RANGE_END=5000
CHAOS_MONKEY_ASSAULTS_EXCEPTIONS_ACTIVE=false
```

Recreate the container with `podman compose up -d --build` after changing `.env`;
`restart` does not reload container environment values. Set both
`CHAOS_MONKEY_ENABLED=false` and `CHAOS_MONKEY_ASSAULTS_LATENCY_ACTIVE=false` to
disable the experiment persistently. Do not commit credentials stored in `.env`.

Check the effective runtime configuration on port **8082**, not port 8081:

```bash
curl --fail http://127.0.0.1:8082/actuator/chaosmonkey/status
curl --fail http://127.0.0.1:8082/actuator/chaosmonkey/assaults
curl --fail http://127.0.0.1:8082/actuator/health
```

The expected initial responses include `"enabled":false` and `"status":"UP"`.
Publishing port 8082 does not enable fault injection. On Podman Compose versions
that cannot recreate Inventory because Order depends on it, recreate the two local
application containers explicitly (this briefly interrupts both APIs):

```bash
podman compose build inventory-service
podman stop order-service inventory-service
podman rm order-service inventory-service
podman compose up -d --no-recreate --no-build inventory-service order-service
```

These commands do not remove database volumes. Do not use `down -v` to resolve
port or container dependency issues.

Actuator updates are temporary and reset to `.env` values when the container is
recreated. To configure exceptions temporarily instead of latency, then enable injection:

```bash
curl http://127.0.0.1:8082/actuator/chaosmonkey/status
curl -X POST http://127.0.0.1:8082/actuator/chaosmonkey/assaults \
  -H 'Content-Type: application/json' \
  -d '{"level":1,"exceptionsActive":true,"latencyActive":false}'
curl -X POST http://127.0.0.1:8082/actuator/chaosmonkey/enable
```

Inventory reservation requests now return the standard sanitized 500 response.
To experiment with latency instead, update the assault configuration:

```bash
curl -X POST http://127.0.0.1:8082/actuator/chaosmonkey/assaults \
  -H 'Content-Type: application/json' \
  -d '{"level":1,"exceptionsActive":false,"latencyActive":true,"latencyRangeStart":2000,"latencyRangeEnd":3000}'
```

Stop injection after the experiment:

```bash
curl -X POST http://127.0.0.1:8082/actuator/chaosmonkey/disable
```

The automated `ChaosMonkeyIntegrationTest` uses random application and management
ports to verify disabled startup, exception injection, health access, and recovery.

## Inventory client timeouts

Order Service uses Spring Boot's injected `RestClient.Builder` so the HTTP client
inherits Boot configuration. In Boot 4.1, the settings are
`spring.http.clients.connect-timeout` and `spring.http.clients.read-timeout`,
without a `restclient` segment. They default to 3s and 5s here and can be overridden
with `INVENTORY_CONNECT_TIMEOUT` and `INVENTORY_READ_TIMEOUT` in `order-service/.env`.
Rebuild and recreate the Order container after code changes; recreate it after
environment changes.

To check the five-second read timeout using Chaos Monkey, inject a delay longer
than five seconds (for example, eight seconds), with only the service watcher
enabled. A delay exactly equal to the timeout is a boundary case. A timeout maps
to the existing 503 inventory-service error and is not retried. A client timeout
does not cancel the Inventory transaction; do not assume the reservation failed
or blindly replay the order.

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
