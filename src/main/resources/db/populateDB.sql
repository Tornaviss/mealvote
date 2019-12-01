DELETE FROM dishes;
DELETE FROM dishes_history;
DELETE FROM menus;
DELETE FROM restaurants;
DELETE FROM user_choices;
DELETE FROM user_choices_history;
DELETE FROM user_roles;
DELETE FROM users;

ALTER SEQUENCE global_seq RESTART WITH 100000;
ALTER SEQUENCE history_seq RESTART WITH 0;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_roles (user_id, role)
VALUES (100000, 'ROLE_USER'),
       (100001, 'ROLE_ADMIN'),
       (100001, 'ROLE_USER');

INSERT INTO restaurants (name)
VALUES ('Dominos Pizza'),
       ('Vegano Huligano'),
       ('Mafia');

INSERT INTO user_choices (user_id, restaurant_id)
VALUES (100000, 100002);
-- (100001, 100003);

INSERT INTO menus (restaurant_id)
VALUES (100002),
       (100003);

INSERT INTO dishes (menu_id, name, price)
VALUES (100002, 'big ass pizza', 1000),
       (100002, 'pepsi', 500),
       (100003, 'falafel with roaches', 10000),
       (100003, 'compotik', 500);
