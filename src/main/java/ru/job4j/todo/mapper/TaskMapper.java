package ru.job4j.todo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskCreateDto toTaskCreateDto(Task task);

    @Mapping(target = "priorityId", source = "priority.id")
    @Mapping(target = "categoryIds", source = "categories")
    TaskUpdateDto toTaskUpdateDto(Task task);

    default Set<Integer> toCategoryIds(Set<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    Task toTask(TaskCreateDto taskCreateDto, Set<Category> categories, Priority priority);

    @Mapping(target = "id", source = "taskUpdateDto.id")
    Task toTask(TaskUpdateDto taskUpdateDto, Set<Category> categories, Priority priority);
}
