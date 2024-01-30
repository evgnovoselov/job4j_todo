package ru.job4j.todo.util;

import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TaskUtil {
    public static Task makeTask(int seed, boolean done) {
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 1, 8, 10);
        Task task = new Task();
        task.setId(seed);
        task.setTitle("Task title " + seed);
        task.setDescription("Task description " + seed);
        task.setCreated(dateTime);
        task.setDone(done);
        Set<Category> categories = IntStream.of(1, 2).mapToObj(i -> {
            Category category = new Category();
            category.setId(i);
            category.setName("Category " + i);
            return category;
        }).collect(Collectors.toSet());
        task.setCategories(categories);
        Priority priority = new Priority();
        priority.setId(1);
        priority.setName("Priority 1");
        priority.setPosition(1);
        task.setPriority(priority);
        return task;
    }
}
