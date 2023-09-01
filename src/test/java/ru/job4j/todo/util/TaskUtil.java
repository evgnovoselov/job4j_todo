package ru.job4j.todo.util;

import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;

public class TaskUtil {
    public static Task makeTask(int seed, boolean done) {
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 1, 8, 10);
        Task task = new Task();
        task.setId(seed);
        task.setTitle("Task title " + seed);
        task.setDescription("Task description " + seed);
        task.setCreated(dateTime);
        task.setDone(done);
        return task;
    }
}
