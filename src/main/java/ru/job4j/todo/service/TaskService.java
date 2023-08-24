package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;

import java.util.Collection;

public interface TaskService {
    Collection<Task> findAllByOrderByCreatedDesc();
}
