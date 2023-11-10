update tasks
set priority_id = (select id from priorities where name = 'urgently');
