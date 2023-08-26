package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository {
    boolean save(Task task);

    boolean update(Task task);

    Collection<Task> findAllByOrderByCreatedDesc();

    Collection<Task> findAllByDoneTrueOrderByCreatedDesc();

    Collection<Task> findAllByDoneFalseOrderByCreatedDesc();

    Optional<Task> findById(int id);

    boolean deleteById(int id);

    boolean setStatusById(int id, boolean done);
}
