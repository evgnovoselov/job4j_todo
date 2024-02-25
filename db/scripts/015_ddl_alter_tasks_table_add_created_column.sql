alter table tasks
    add column created timestamp without time zone default now();
