package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository {
    Optional<Integer> save(Task task);

    boolean update(Task task);

    Collection<Task> findAllByOrderByCreatedDesc();

    Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done);

    Optional<Task> findById(int id);

    boolean deleteById(int id);

    boolean setStatusById(int id, boolean done);
}
