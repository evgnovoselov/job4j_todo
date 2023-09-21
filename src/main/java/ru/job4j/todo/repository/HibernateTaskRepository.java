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
    public boolean save(Task task) {
        try {
            crudRepository.run(session -> session.persist(task));
            return true;
        } catch (Exception e) {
            Task errorTask = task;
            if (errorTask == null) {
                errorTask = new Task();
            }
            log.error("Error save task, title = {}", errorTask.getTitle());
        }
        return false;
    }

    @Override
    public boolean update(Task task) {
        try {
            crudRepository.run(session -> session.update(task));
            return true;
        } catch (Exception e) {
            Task errorTask = task;
            if (errorTask == null) {
                errorTask = new Task();
            }
            log.error("Error update task, id = {}", errorTask.getId());
        }
        return false;
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc() {
        try {
            return crudRepository.query("from Task order by created desc", Task.class);
        } catch (Exception e) {
            log.error("Error find all tasks order by created desc");
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done) {
        try {
            return crudRepository.query(
                    "from Task where done = :done order by created desc",
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
            return crudRepository.optional(
                    "from Task where id = :id",
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
