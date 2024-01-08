package ru.job4j.todo.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaskMapper {
    private final CategoryService categoryService;
    private final PriorityService priorityService;

    public TaskCreateDto toTaskCreateDto(Task task) {
        Integer priorityId = null;
        if (task.getPriority() != null) {
            priorityId = task.getPriority().getId();
        }
        return new TaskCreateDto(
                task.getTitle(),
                task.getCategories().stream().map(Category::getId).collect(Collectors.toSet()),
                priorityId,
                task.getDescription()
        );
    }

    public Task toTask(TaskCreateDto taskCreateDto) {
        Task task = new Task();
        task.setTitle(taskCreateDto.title());
        task.setDescription(taskCreateDto.description());
        taskCreateDto
                .categoryIds().forEach(categoryId -> categoryService.findById(categoryId)
                        .ifPresent(category -> task.getCategories().add(category)));
        priorityService.findById(taskCreateDto.priorityId()).ifPresent(task::setPriority);
        return task;
    }
}