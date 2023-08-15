create table tasks
(
    id          serial primary key,
    description text,
    created     timestamp,
    done        boolean
)
