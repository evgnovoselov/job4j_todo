package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.dto.TaskUpdateDto;
import ru.job4j.todo.mapper.TaskMapper;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.CategoryRepository;
import ru.job4j.todo.repository.PriorityRepository;
import ru.job4j.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SimpleTaskService implements TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskCreateDto getEmptyTaskCreateDto() {
        return taskMapper.toTaskCreateDto(new Task());
    }

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc() {
        return taskRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public Collection<Task> findAllByDoneOrderByCreatedDesc(boolean done) {
        return taskRepository.findAllByDoneOrderByCreatedDesc(done);
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    @Override
    public Optional<TaskUpdateDto> getTaskUpdateDtoById(int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(taskMapper.toTaskUpdateDto(taskOptional.get()));
    }

    @Override
    public Optional<Integer> save(TaskCreateDto taskCreateDto, User user) {
        Set<Category> categories = getCategories(taskCreateDto.categoryIds());
        Priority priority = getPriority(taskCreateDto.priorityId());
        Task task = taskMapper.toTask(taskCreateDto, categories, priority);
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            task.setTitle("Задача без названия");
        }
        task.setCreated(LocalDateTime.now());
        task.setUser(user);
        return taskRepository.save(task);
    }

    private Priority getPriority(Integer priorityId) {
        return priorityRepository.findById(priorityId).orElse(null);

    }

    private Set<Category> getCategories(Set<Integer> categoryIds) {
        return categoryIds.stream()
                .<Category>mapMulti((categoryId, categoryConsumer) -> categoryRepository.findById(categoryId).ifPresent(categoryConsumer))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean setStatusById(int id, boolean done) {
        return taskRepository.setStatusById(id, done);
    }

    @Override
    public boolean deleteById(int id) {
        return taskRepository.deleteById(id);
    }

    @Override
    public boolean update(TaskUpdateDto taskUpdateDto) {
        Set<Category> categories = getCategories(taskUpdateDto.categoryIds());
        Priority priority = getPriority(taskUpdateDto.priorityId());
        Task task = taskMapper.toTask(taskUpdateDto, categories, priority);
        Optional<Task> oldTask = taskRepository.findById(task.getId());
        if (oldTask.isEmpty()) {
            return false;
        }
        task.setCreated(oldTask.get().getCreated());
        task.setUser(oldTask.get().getUser());
        return taskRepository.update(task);
    }
}
