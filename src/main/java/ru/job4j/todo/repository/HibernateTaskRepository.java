package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class HibernateTaskRepository implements TaskRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Integer> save(Task task) {
        try {
            crudRepository.run(session -> session.persist(task));
            return Optional.ofNullable(task.getId());
        } catch (Exception e) {
            log.error("Error save task, title = {}", task.getTitle());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Task task) {
        try {
            crudRepository.run(session -> session.update(task));
            return true;
        } catch (Exception e) {
            log.error("Error update task, id = {}", task.getId());
        }
        return false;
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc() {
        try {
            String hql = """
                    from Task t
                    left join fetch t.priority
                    left join fetch t.categories
                    order by t.created desc
                    """;
            return crudRepository.query(hql, Task.class);
        } catch (Exception e) {
            log.error("Error find all tasks order by created desc");
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done) {
        try {
            String hql = """
                    from Task t
                    left join fetch t.priority
                    left join fetch t.categories
                    where t.done = :done
                    order by t.created desc
                    """;
            return crudRepository.query(
                    hql,
                    Task.class,
                    Map.of("done", done)
            );
        } catch (Exception e) {
            log.error("Error find all tasks by done {} order by created desc", done);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Task> findById(int id) {
        try {
            String hql = """
                    from Task t
                    left join fetch t.priority
                    left join fetch t.categories
                    where t.id = :id
                    """;
            return crudRepository.optional(
                    hql,
                    Task.class,
                    Map.of("id", id)
            );
        } catch (Exception e) {
            log.error("Error find task by id = {}", id);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(int id) {
        try {
            return crudRepository.run(
                    "delete from Task where id = :id",
                    Map.of("id", id)
            );
        } catch (Exception e) {
            log.error("Error delete task by id = {}", id);
        }
        return false;
    }

    @Override
    public boolean setStatusById(int id, boolean done) {
        try {
            return crudRepository.run(
                    "update Task set done = :done where id = :id",
                    Map.of(
                            "done", done,
                            "id", id
                    )
            );
        } catch (Exception e) {
            log.error("Error set task status done {} by id = {}", done, id);
        }
        return false;
    }
}
