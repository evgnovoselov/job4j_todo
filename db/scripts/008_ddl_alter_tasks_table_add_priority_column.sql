alter table tasks
    add column priority_id int references priorities (id);
