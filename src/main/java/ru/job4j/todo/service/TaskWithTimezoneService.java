package ru.job4j.todo.service;

import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskWithTimezoneService extends TaskService {
    Collection<Task> findAllByOrderByCreatedDesc(String timezoneId);

    Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done, String timezoneId);

    Optional<Task> findById(int id, String timezoneId);

    Optional<TaskUpdateDto> getTaskUpdateDtoById(int id, String timezoneId);
}
