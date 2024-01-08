package ru.job4j.todo.service;

import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    TaskCreateDto getEmptyTaskCreateDto();

    Collection<Task> findAllByOrderByCreatedDesc();

    Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done);

    Optional<Task> findById(int id);

    Optional<TaskUpdateDto> getTaskUpdateDtoById(int id);

    Optional<Integer> save(TaskCreateDto taskCreateDto, User user);

    boolean setStatusById(int id, boolean done);

    boolean deleteById(int id);

    boolean update(TaskUpdateDto taskUpdateDto);
}
