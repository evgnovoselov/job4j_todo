package ru.job4j.todo.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskUpdateDto(
        int id,
        String title,
        String description,
        Set<Integer> categoryIds,
        Integer priorityId,
        boolean done,
        LocalDateTime created
) {
}
