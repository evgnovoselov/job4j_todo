create table tasks
(
    id          serial primary key,
    title       text,
    description text,
    created     timestamp,
    done        boolean
)
