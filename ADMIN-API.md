# Admin restaurant and dish management

Log in with `POST http://72.61.114.40:8084/api/login` using the existing admin email/password.
Send the returned token as `Authorization: Bearer <token>` on writes. Old demo tokens no longer work.
Sessions expire after eight hours and are invalidated when user-service restarts.
Public registration defaults to CUSTOMER; creating ADMIN or RESTAURANT_MANAGER accounts requires an admin token.

| Method | Gateway path | Access |
| --- | --- | --- |
| POST | /api/restaurants | Admin |
| PUT | /api/restaurants/{id} | Admin |
| GET | /api/restaurants | Public |
| GET | /api/restaurants/{id} | Public |
| POST | /api/restaurants/{id}/dishes | Admin |
| PUT | /api/restaurants/{id}/dishes/{dishId} | Admin |
| GET | /api/restaurants/{id}/dishes | Public |
| GET | /api/dishes/{id} | Public |

Restaurant create/update JSON:
```json
{"name":"Foodie Kitchen","address":"Dubai","phone":"+971501234567","rating":4.5}
```
Dish create/update JSON:
```json
{"name":"Burger","description":"Grilled burger","price":25.00,"category":"MAIN"}
```
PUT replaces editable fields; send all required fields. Rating is optional (0–5 with at most one decimal place).
Price must be positive with at most two decimal places. Create returns 201, update returns 200.
Responses include the saved fields plus restaurantId (and dishId for dishes).
Missing/invalid tokens return 401, non-admin tokens return 403, missing resources return 404, invalid input returns 400.

The existing admin account must be retained. On a fresh empty installation, provision the first admin through a trusted database/bootstrap process; public self-promotion is not allowed.
This change does not add restaurant-manager ownership permissions or secure unrelated order/user APIs.
