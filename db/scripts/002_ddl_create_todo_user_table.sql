create table todo_user
(
    id       serial primary key,
    login    text unique not null,
    password text        not null,
    name     text        not null
)
