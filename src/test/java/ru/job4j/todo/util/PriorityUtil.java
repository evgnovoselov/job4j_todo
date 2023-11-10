package ru.job4j.todo.util;

import ru.job4j.todo.model.Priority;

public final class PriorityUtil {
    public static Priority makePriority(int seed) {
        Priority priority = new Priority();
        priority.setId(seed);
        priority.setName("Priority " + seed);
        priority.setPosition(seed);
        return priority;
    }
}
