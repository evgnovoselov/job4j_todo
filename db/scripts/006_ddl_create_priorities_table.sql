create table priorities
(
    id       serial primary key,
    name     text unique not null,
    position int         not null
);
