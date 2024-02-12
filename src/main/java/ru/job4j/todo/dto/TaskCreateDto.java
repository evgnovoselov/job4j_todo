package ru.job4j.todo.dto;

import java.util.Set;

public record TaskCreateDto(
        String title,
        Set<Integer> categoryIds,
        Integer priorityId,
        String description
) {
}
