# FlashSale Engine

FlashSale Engine is a Spring Boot microservices project for handling high-concurrency ticket reservations. It combines PostgreSQL for durable application data, Redis/Lua scripts for atomic inventory operations, and Apache Kafka for asynchronous events between services.

## Services

| Service | Responsibility | Default port |
| --- | --- | ---: |
| `booking-service` | Creates events and reservations; manages idempotency and the outbox | 8080 |
| `inventory-service` | Reserves/releases inventory in Redis and publishes inventory events | 8083 |
| `reservation-service` | Consumes reservation-created events | 8080 |
| `payment-service` | Consumes reservation-created events and publishes payment events | 8080 |

The three services configured for port 8080 cannot run simultaneously with their default settings. The commands below assign ports to the background services when running the complete stack locally.

## Architecture

1. A client creates an event through the booking service.
2. A reservation request atomically reserves inventory in Redis.
3. The booking service persists the reservation and an outbox event in PostgreSQL.
4. The outbox publisher sends events to Kafka.
5. Reservation, payment, and inventory consumers process the relevant Kafka topics.
6. Inventory events can be relayed from a Redis Stream to Kafka.

The reservation flow uses an `Idempotency-Key` header to make retries safe when the same request is submitted more than once.

## Prerequisites

- Java 21
- Maven 3.9+ (or the Maven Wrapper included in each service)
- Docker and Docker Compose
- k6, if you want to run the load tests

## Start infrastructure

From the repository root:

```bash
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432` (`flashsale` / `flashsale`)
- Redis on `localhost:6379`
- Kafka on `localhost:9092`
- Payment PostgreSQL on `localhost:5433` (`payment_db`)
- Inventory PostgreSQL on `localhost:5434` (`inventory_db`)

To stop the containers while preserving their volumes:

```bash
docker compose down
```

## Build and test

Each service is an independent Maven project. Run the following from the repository root:

```bash
for service in booking-service inventory-service reservation-service payment-service; do
  (cd "$service" && ./mvnw clean verify)
done
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Run the services

Open one terminal per service after starting the infrastructure:

```bash
cd booking-service && ./mvnw spring-boot:run
```

```bash
cd inventory-service && ./mvnw spring-boot:run
```

Run reservation and payment on alternate ports because both default to 8080:

```bash
cd reservation-service && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

```bash
cd payment-service && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
```

The booking service is available at `http://localhost:8080` and exposes actuator endpoints such as `/actuator/health`.

## API examples

Check the booking service:

```bash
curl http://localhost:8080/health
```

Create an event and its ticket types:

```bash
curl -X POST http://localhost:8080/api/v1/events \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "FlashSale Concert",
    "venue": "Main Arena",
    "eventTime": "2099-12-31T20:00:00",
    "ticketTypes": [
      {"name": "General Admission", "price": 49.99, "quantity": 1000}
    ]
  }'
```

Create a reservation. Replace `1` with the ticket type ID returned when the event is created:

```bash
curl -X POST http://localhost:8080/api/v1/reservations \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: reservation-demo-001' \
  -d '{
    "userId": "user-123",
    "ticketTypeId": 1,
    "quantity": 1
  }'
```

The legacy direct inventory endpoints are also available on the booking service:

```bash
curl http://localhost:8080/api/v1/inventory/1
curl -X POST 'http://localhost:8080/api/v1/inventory/1/reserve?quantity=1'
curl -X POST 'http://localhost:8080/api/v1/inventory/1/release?quantity=1'
```

## Load tests

The `load-test` directory contains k6 scenarios for reservations, concurrency, and direct Redis inventory operations:

```bash
k6 run load-test/reservation-test.js
k6 run load-test/concurrency-test.js
k6 run load-test/redis-inventory-test.js
```

The reservation tests expect the booking service at `localhost:8080` and use ticket type ID `1`.

## Project structure

```text
.
├── booking-service/
├── inventory-service/
├── reservation-service/
├── payment-service/
├── load-test/
└── docker-compose.yml
```

## Configuration

Service configuration is stored in each module's `src/main/resources/application.yml`. The current configuration targets local infrastructure and uses `ddl-auto: update` for the JPA-backed services. For production, provide credentials and connection details through environment-specific configuration or environment variables, and use a managed database migration strategy.

## Current limitations

- The repository does not currently provide container images or a single command to start all four Spring Boot services.
- Kafka topic creation is disabled in Docker Compose; required topics must be created by the application or provisioned separately.
- The service configuration is optimized for local development and contains development database credentials.
- The direct inventory controller is marked deprecated and is retained mainly for testing the Redis inventory path.
