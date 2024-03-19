package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.mapper.TaskMapper;
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

@Service
public class SimpleTaskWithTimezoneService extends SimpleTaskService implements TaskWithTimezoneService {
    private static final Set<String> TIMEZONE_IDS = Collections.unmodifiableSet(TimeZoneUtil.getTimeZoneIds());

    public SimpleTaskWithTimezoneService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            PriorityRepository priorityRepository,
            TaskMapper taskMapper
    ) {
        super(taskRepository, categoryRepository, priorityRepository, taskMapper);
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc(String timezoneId) {
        Collection<Task> tasks = findAllByOrderByCreatedDesc();
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
        return null;
    }

    @Override
    public Optional<Task> findById(int id, String timezoneId) {
        return Optional.empty();
    }

    @Override
    public Optional<TaskUpdateDto> getTaskUpdateDtoById(int id, String timezoneId) {
        return Optional.empty();
    }
}
