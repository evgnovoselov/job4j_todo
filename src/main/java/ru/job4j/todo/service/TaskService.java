package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    Collection<Task> findAllByOrderByCreatedDesc();

    Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done);

    Optional<Task> findById(int id);

    boolean save(Task task);

    boolean setStatusById(int id, boolean done);

    boolean deleteById(int id);

    boolean update(Task task);
}
