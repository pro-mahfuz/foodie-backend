# Customer cart and ordering

Base URL: http://72.61.114.40:8084

1. POST /api/login with email and password; retain the returned customerId and token.
2. Send Authorization: Bearer <token> on every cart/order call. Only CUSTOMER sessions may use these endpoints, for their own account.
3. POST /api/customers/{customerId}/cart/items with {"dishId":1,"quantity":2}. This creates the cart implicitly on the first item.
4. GET /api/customers/{customerId}/cart to see items and totals.
5. POST /api/customers/{customerId}/cart/checkout with no body. Returns 201 and the order; the cart is cleared in the same transaction. Empty carts return 400. Prices are rechecked against the food service.
6. GET /api/orders/{orderId} to view your order.

DELETE /api/customers/{customerId}/cart clears your cart without ordering.

Direct ordering remains available: POST /api/orders with {"restaurantId":1,"items":[{"dishId":1,"quantity":2}]}. customerId is derived from the login session; if supplied it must match. Direct ordering does not clear the cart.

All items must belong to one restaurant. Quantities must be positive. Missing/invalid tokens return 401; non-customer roles or another customer's ID return 403.
Checkout has no idempotency key: do not submit concurrent checkout requests or retry automatically after an ambiguous network result.
