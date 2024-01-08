package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.dto.TaskCreateDto;
import ru.job4j.todo.mapper.TaskMapper;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleTaskService implements TaskService {
    private final TaskRepository taskRepository;
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
    public Optional<Integer> save(TaskCreateDto taskCreateDto, User user) {
        Task task = taskMapper.toTask(taskCreateDto);
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            task.setTitle("Задача без названия");
        }
        task.setCreated(LocalDateTime.now());
        task.setUser(user);
        return taskRepository.save(task);
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
    public boolean update(Task task) {
        Optional<Task> oldTask = taskRepository.findById(task.getId());
        if (oldTask.isEmpty()) {
            return false;
        }
        task.setCreated(oldTask.get().getCreated());
        task.setUser(oldTask.get().getUser());
        return taskRepository.update(task);
    }
}
