# Foodie Backend

Spring Boot REST proof of concept using Java 17, MySQL, JPA, validation, Docker, and Jenkins.

## Run

```bash
docker compose up --build
```

The API is available at `http://localhost:8080/api`.

MySQL is exposed to the host at `localhost:3307`. The API container connects
to it through the Docker network at `mysql:3306`.

For local development without MySQL, run the packaged application with the H2 profile:

```bash
java -jar target/foodie-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## Endpoints

- `GET /api/restaurants`
- `GET /api/restaurants/{id}`
- `GET /api/restaurants/{id}/dishes`
- `GET /api/dishes/{id}`
- `POST /api/users`
- `POST /api/login`
- `GET /api/customers/{customerId}/cart`
- `POST /api/customers/{customerId}/cart/items`
- `DELETE /api/customers/{customerId}/cart`
- `POST /api/orders`
- `GET /api/orders/{id}`

Example order request:

```json
{"customerId":1,"restaurantId":1,"items":[{"dishId":1,"quantity":2}]}
```

Order totals and captured unit prices are calculated by the server. Every dish in an order must belong to the selected restaurant.

## Authentication note

The original customer model did not contain credentials. A `password_hash` column was added so passwords can be stored with BCrypt. The login token in this proof of concept is deliberately marked `Demo` and does not authorize protected routes; replace it with signed JWT authentication before production use.

The cart is in memory because the supplied database model has no cart tables. It is lost when the API restarts. Add `cart` and `cart_item` tables for persistent carts.
