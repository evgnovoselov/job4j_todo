package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    Collection<Task> findAllByOrderByCreatedDesc();

    Collection<Task> findAllByDoneTrueOrderByCreatedDesc();

    Collection<Task> findAllByDoneFalseOrderByCreatedDesc();

    Optional<Task> findById(int id);

    boolean save(Task task);

    boolean setStatusById(int id, boolean done);
}
