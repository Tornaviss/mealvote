DROP TABLE IF EXISTS dishes_history;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS user_choices;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_choices_history;
DROP SEQUENCE IF EXISTS global_seq;
DROP SEQUENCE IF EXISTS history_seq;

CREATE SEQUENCE global_seq START WITH 100000;
CREATE SEQUENCE history_seq START WITH 0;

CREATE TABLE users
(
    id         INTEGER   DEFAULT global_seq.nextval PRIMARY KEY,
    name       VARCHAR                 NOT NULL,
    email      VARCHAR                 NOT NULL,
    password   VARCHAR                 NOT NULL,
    registered TIMESTAMP DEFAULT now() NOT NULL,
    enabled    BOOL      DEFAULT TRUE  NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurants
(
    id   INTEGER DEFAULT global_seq.nextval PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE user_choices
(
    user_id       INTEGER NOT NULL,
    restaurant_id INTEGER NOT NULL,
    date_time     TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT user_id_idx UNIQUE (user_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE menus
(
    restaurant_id INTEGER            NOT NULL,
    date          DATE DEFAULT now() NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE,
    CONSTRAINT restaurant_unique_idx UNIQUE (restaurant_id)
);

CREATE TABLE dishes
(
    id      INTEGER DEFAULT global_seq.nextval PRIMARY KEY,
    menu_id INTEGER NOT NULL,
    name    VARCHAR NOT NULL,
    price   INTEGER NOT NULL,
    CONSTRAINT dish_name_menu_unique_idx UNIQUE (menu_id, name),
    FOREIGN KEY (menu_id) REFERENCES menus (restaurant_id) ON DELETE CASCADE
);

CREATE TABLE dishes_history
(
    history_id INTEGER DEFAULT history_seq.nextval PRIMARY KEY,
    id         INTEGER              NOT NULL,
    menu_id    INTEGER              NOT NULL,
    name       VARCHAR              NOT NULL,
    price      INTEGER              NOT NULL,
    username   VARCHAR              NOT NULL,
    action     VARCHAR              NOT NULL,
    action_timestamp  TIMESTAMP            NOT NULL,
    active     BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TRIGGER dishes_update_audit_trail
    AFTER UPDATE
    ON dishes
    FOR EACH ROW
CALL "com.mealvote.audition.DishAuditTrigger";

CREATE TRIGGER dishes_delete_audit_trail
    AFTER DELETE
    ON dishes
    FOR EACH ROW
CALL "com.mealvote.audition.DishAuditTrigger";

CREATE TRIGGER dishes_create_audit_trail
    AFTER INSERT
    ON dishes
    FOR EACH ROW
CALL "com.mealvote.audition.DishAuditTrigger";

CREATE TABLE user_choices_history
(
    history_id       INTEGER DEFAULT history_seq.nextval PRIMARY KEY,
    user_id          INTEGER              NOT NULL,
    restaurant_id    INTEGER              NOT NULL,
    date_time        TIMESTAMP(0)         NOT NULL,
    username         VARCHAR              NOT NULL,
    action_timestamp TIMESTAMP            NOT NULL,
    action           VARCHAR              NOT NULL,
    active           BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TRIGGER user_choices_create_audit_trigger
    AFTER INSERT
    ON user_choices
    FOR EACH ROW
CALL "com.mealvote.audition.ChoiceAuditTrigger";

CREATE TRIGGER user_choices_update_audit_trigger
    AFTER UPDATE
    ON user_choices
    FOR EACH ROW
CALL "com.mealvote.audition.ChoiceAuditTrigger";

CREATE TRIGGER user_choices_delete_audit_trigger
    AFTER DELETE
    ON user_choices
    FOR EACH ROW
CALL "com.mealvote.audition.ChoiceAuditTrigger";
