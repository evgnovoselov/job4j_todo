create table tasks_categories
(
    task_id     int not null references tasks (id) on delete cascade,
    category_id int not null references categories (id) on delete cascade,
    primary key (task_id, category_id)
);