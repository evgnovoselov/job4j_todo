package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.mapper.TaskMapper;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.CategoryRepository;
import ru.job4j.todo.repository.PriorityRepository;
import ru.job4j.todo.repository.TaskRepository;
import ru.job4j.todo.util.TimeZoneUtil;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleTaskWithTimezoneService extends SimpleTaskService implements TaskWithTimezoneService {
    private static final Set<String> TIMEZONE_IDS = Collections.unmodifiableSet(TimeZoneUtil.getTimeZoneIds());
    private final TaskMapper taskMapper;

    public SimpleTaskWithTimezoneService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            PriorityRepository priorityRepository,
            TaskMapper taskMapper
    ) {
        super(taskRepository, categoryRepository, priorityRepository, taskMapper);
        this.taskMapper = taskMapper;
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc(String timezoneId) {
        Collection<Task> tasks = findAllByOrderByCreatedDesc();
        if (TIMEZONE_IDS.contains(timezoneId)) {
            editTasksFieldCreatedToTimezone(tasks, timezoneId);
        }
        return tasks;
    }

    private static void editTasksFieldCreatedToTimezone(Collection<Task> tasks, String timezoneId) {
        tasks.forEach(task -> editTaskFieldCreatedToTimezone(task, timezoneId));
    }

    private static void editTaskFieldCreatedToTimezone(Task task, String timezoneId) {
        task.setCreated(
                task.getCreated()
                        .atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.of(timezoneId))
                        .toLocalDateTime());
    }

    @Override
    public Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done, String timezoneId) {
        Collection<Task> tasks = findAllByDoneOrderByCreatedDesc(done);
        if (TIMEZONE_IDS.contains(timezoneId)) {
            editTasksFieldCreatedToTimezone(tasks, timezoneId);
        }
        return tasks;
    }

    @Override
    public Optional<Task> findById(int id, String timezoneId) {
        Optional<Task> taskOptional = findById(id);
        if (TIMEZONE_IDS.contains(timezoneId)) {
            taskOptional.ifPresent(task -> editTaskFieldCreatedToTimezone(task, timezoneId));
        }
        return taskOptional;
    }

    @Override
    public Optional<TaskUpdateDto> getTaskUpdateDtoById(int id, String timezoneId) {
        Optional<TaskUpdateDto> taskUpdateDtoOptional = getTaskUpdateDtoById(id);
        if (taskUpdateDtoOptional.isEmpty()) {
            return Optional.empty();
        }
        if (TIMEZONE_IDS.contains(timezoneId)) {
            Task task = taskMapper.toTask(
                    taskUpdateDtoOptional.get(),
                    taskUpdateDtoOptional.get().categoryIds().stream().map(categoryId -> {
                        Category category = new Category();
                        category.setId(categoryId);
                        return category;
                    }).collect(Collectors.toSet()),
                    taskUpdateDtoOptional.map(taskUpdateDto -> {
                        Priority priority = new Priority();
                        priority.setId(taskUpdateDto.priorityId());
                        return priority;
                    }).get()
            );
            editTaskFieldCreatedToTimezone(task, timezoneId);
            taskUpdateDtoOptional = Optional.of(taskMapper.toTaskUpdateDto(task));
        }
        return taskUpdateDtoOptional;
    }
}
