alter table tasks
add user_id int references todo_user(id);
