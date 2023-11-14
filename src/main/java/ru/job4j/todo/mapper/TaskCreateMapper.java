package ru.job4j.todo.mapper;

import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.model.Task;

public class TaskCreateMapper {
    public TaskCreateDto getDto(Task task) {
        return new TaskCreateDto();
    }

    public Task getTask(TaskCreateDto taskCreateDto) {
        return new Task();
    }
}
