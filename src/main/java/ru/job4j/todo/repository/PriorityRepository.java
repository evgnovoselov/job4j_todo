package ru.job4j.todo.repository;

import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface PriorityRepository {
    boolean save(Priority priority);

    Optional<Priority> findById(int id);

    Collection<Priority> findAllByOrderByPosition();

    boolean deleteById(int id);

}
