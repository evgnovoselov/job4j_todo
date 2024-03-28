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
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
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
        // TODO: 22.03.2024 Duplicate code
        if (TIMEZONE_IDS.contains(timezoneId)) {
            tasks = tasks.stream()
                    .peek(task -> task.setCreated(
                            task.getCreated()
                                    .atZone(ZoneId.of("UTC"))
                                    .withZoneSameInstant(ZoneId.of(timezoneId))
                                    .toLocalDateTime()
                    ))
                    .toList();
        }
        return tasks;
    }

    @Override
    public Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done, String timezoneId) {
        Collection<Task> tasks = findAllByDoneOrderByCreatedDesc(done);
        // TODO: 22.03.2024 Duplicate code
        if (TIMEZONE_IDS.contains(timezoneId)) {
            tasks = tasks.stream()
                    .peek(task -> task.setCreated(
                            task.getCreated()
                                    .atZone(ZoneId.of("UTC"))
                                    .withZoneSameInstant(ZoneId.of(timezoneId))
                                    .toLocalDateTime()
                    ))
                    .toList();
        }
        return tasks;
    }

    @Override
    public Optional<Task> findById(int id, String timezoneId) {
        Optional<Task> taskOptional = findById(id);
        // TODO: 22.03.2024 Duplicate code
        if (TIMEZONE_IDS.contains(timezoneId)) {
            taskOptional.ifPresent(task -> task.setCreated(
                    task.getCreated()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameInstant(ZoneId.of(timezoneId))
                            .toLocalDateTime()
            ));
        }
        return taskOptional;
    }

    @Override
    public Optional<TaskUpdateDto> getTaskUpdateDtoById(int id, String timezoneId) {
        Optional<TaskUpdateDto> taskUpdateDtoOptional = getTaskUpdateDtoById(id);
        if (taskUpdateDtoOptional.isEmpty()) {
            return Optional.empty();
        }
        // TODO: 22.03.2024 Duplicate code
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
            task.setCreated(
                    task.getCreated()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameInstant(ZoneId.of(timezoneId))
                            .toLocalDateTime()
            );
            taskUpdateDtoOptional = Optional.of(taskMapper.toTaskUpdateDto(task));
        }
        return taskUpdateDtoOptional;
    }
}
