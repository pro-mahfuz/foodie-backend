# Foodie Microservices

Three independently deployable domain services, each owning its own MySQL database, fronted by Spring Cloud Gateway. Spring Cloud Config centralizes runtime configuration and Eureka provides service registration, discovery, and client-side load balancing.

| Service | Port | Database | Responsibility |
|---|---:|---|---|
| user-service | 8081 | user_db | registration, login, customer lookup |
| food-service | 8082 | food_db | restaurants and dishes |
| order-service | 8083 | order_db | persistent carts and orders |
| api-gateway | 8080 | none | public API routing |
| config-server | 8888 | none | centralized service configuration |
| eureka-server | 8761 | none | service registry and discovery dashboard |

## Run

```bash
docker compose up --build
```

The public API is available through the gateway at `http://localhost:8080/api`. Domain-service ports are internal to the Compose network. Build all modules locally with `mvn clean verify`. Service URLs and database credentials can be overridden with the environment variables in `compose.yml`.

Infrastructure endpoints:

- Eureka dashboard: `http://localhost:8761`
- Config Server: `http://localhost:8888/{application}/{profile}`
- Gateway health: `http://localhost:8080/actuator/health`

Centralized configuration files are under `config-server/src/main/resources/config`. The gateway and order service resolve domain services through Eureka using logical names such as `lb://user-service` instead of fixed container addresses.

## Docker databases

MySQL 8.4 runs entirely in Docker with one isolated database container and named volume per domain service:

| Container | Database | Application | Volume |
|---|---|---|---|
| user-db | user_db | user-service | user-data |
| food-db | food_db | food-service | food-data |
| order-db | order_db | order-service | order-data |

The databases are reachable only inside the Compose network on port `3306`; they are not exposed publicly on the host. Credentials and JDBC URLs are supplied through `compose.yml`, while Config Server contains matching Docker defaults.

## Jenkins

Run the isolated Jenkins CI stack with:

```bash
docker compose -f jenkins-compose.yml up --build -d
```

Jenkins is available at `http://localhost:9090`. Its configuration and internal Docker daemon are persisted in named volumes. Applications deployed by this Jenkins Docker daemon are exposed through `18080` (gateway), `18761` (Eureka), and `18888` (Config Server) to avoid conflicting with the development stack.

## Endpoints

- user-service (`:8081`): `POST /api/users`, `POST /api/login`, `GET /api/users/{id}`
- food-service (`:8082`): `GET /api/restaurants`
- `GET /api/restaurants/{id}`
- `GET /api/restaurants/{id}/dishes`
- `GET /api/dishes/{id}`
- order-service (`:8083`): `GET /api/customers/{customerId}/cart`
- `POST /api/customers/{customerId}/cart/items`
- `DELETE /api/customers/{customerId}/cart`
- `POST /api/orders`
- `GET /api/orders/{id}`

Example order request:

```json
{"customerId":1,"restaurantId":1,"items":[{"dishId":1,"quantity":2}]}
```

Order totals and captured unit prices are calculated by the server. Every dish must belong to the selected restaurant. Orders keep external IDs and dish snapshots rather than cross-database foreign keys. The order service validates users and dishes via HTTP.

## Authentication note

The original customer model did not contain credentials. A `password_hash` column was added so passwords can be stored with BCrypt. The login token in this proof of concept is deliberately marked `Demo` and does not authorize protected routes; replace it with signed JWT authentication before production use.

Cart items are persisted in `order_db`.
