package ru.job4j.todo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.TaskRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleTaskService implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Collection<Task> findAllByOrderByCreatedDesc() {
        return taskRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public Collection<Task> findAllByDoneTrueOrderByCreatedDesc() {
        return taskRepository.findAllByDoneTrueOrderByCreatedDesc();
    }

    @Override
    public Collection<Task> findAllByDoneFalseOrderByCreatedDesc() {
        return taskRepository.findAllByDoneFalseOrderByCreatedDesc();
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }
}
