INSERT IGNORE INTO restaurant (restaurant_id, name, address, phone, rating)
VALUES (1, 'Foodie Kitchen', 'Downtown Dubai, UAE', '+971501112233', 4.7);

INSERT IGNORE INTO dish (dish_id, restaurant_id, name, description, price, category)
VALUES
  (1, 1, 'Classic Burger', 'Beef burger with lettuce, tomato, cheese, and house sauce', 39.00, 'Burgers'),
  (2, 1, 'Margherita Pizza', 'Tomato, mozzarella, and fresh basil', 42.00, 'Pizza'),
  (3, 1, 'Caesar Salad', 'Romaine lettuce, parmesan, croutons, and Caesar dressing', 28.00, 'Salads');
