create table categories
(
    id   serial primary key,
    name text unique not null
);